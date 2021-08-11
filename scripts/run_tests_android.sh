#!/bin/bash

###############################################################################
# README
###############################################################################
# This script runs the Android Instrumentation Tests of MobileRT on an
# Android emulator.
###############################################################################
###############################################################################


###############################################################################
# Change directory to MobileRT root
###############################################################################
cd "$(dirname "${BASH_SOURCE[0]}")/.." || exit
###############################################################################
###############################################################################


###############################################################################
# Exit immediately if a command exits with a non-zero status
###############################################################################
set -euo pipefail;
###############################################################################
###############################################################################


###############################################################################
# Set default arguments
###############################################################################
type="release";
run_test="all";
ndk_version="21.3.6528147";
cmake_version="3.10.2";
kill_previous="true";

function printEnvironment() {
  echo "";
  echo "Selected arguments:";
  echo "type: ${type}";
  echo "run_test: ${run_test}";
  echo "ndk_version: ${ndk_version}";
  echo "cmake_version: ${cmake_version}";
  echo "kill_previous: ${kill_previous}";
}
###############################################################################
###############################################################################


###############################################################################
# Set paths
###############################################################################
echo "Set path to reports"
reports_path="./app/build/reports"

echo "Set path to instrumentation tests resources"
mobilert_path="/data/MobileRT"
sdcard_path="/mnt/sdcard/MobileRT"
###############################################################################
###############################################################################


###############################################################################
# Get helper functions
###############################################################################
source scripts/helper_functions.sh
###############################################################################
###############################################################################


###############################################################################
# Parse arguments
###############################################################################
parseArgumentsToTestAndroid "$@";
printEnvironment;
###############################################################################
###############################################################################


###############################################################################
# Helper functions
###############################################################################
function gather_logs_func() {
  echo ""
  echo ""
  echo "Gathering logs"

  echo "Copy logcat to file"
  callCommand adb logcat -v threadtime -d "*":V \
    > "${reports_path}"/logcat_"${type}".log 2>&1

  local pid_app
  pid_app=$(grep -E -i "proc.puscas:*" "${reports_path}"/logcat_"${type}".log |
    grep -i "pid=" | cut -d "=" -f 2 | cut -d "u" -f 1 | tr -d ' ' | tail -1)
  echo "Filter logcat of the app: ${pid_app}"
  callCommand cat "${reports_path}"/logcat_"${type}".log | grep -e "${pid_app}" -e "I DEBUG" \
      > "${reports_path}"/logcat_app_"${type}".log

  echo "Filter realtime logcat of the app"
  callCommand cat "${reports_path}"/logcat_current_"${type}".log |
    grep -E -i "$(grep -E -i "proc.*:puscas" \
      "${reports_path}"/logcat_current_"${type}".log |
      cut -d ":" -f 4 | cut -d ' ' -f 4)" \
      > "${reports_path}"/logcat_current_app_"${type}".log

  echo -e '\e]8;;file:///'"${PWD}"'/'"${reports_path}"'/androidTests/connected/index.html\aClick here to check the Android tests report.\e]8;;\a'
  echo -e '\e]8;;file:///'"${PWD}"'/'"${reports_path}"'/coverage/'"${type}"'/index.html\aClick here to check the Code coverage report.\e]8;;\a'
  echo -e '\e]8;;file:///'"${PWD}"'/'"${reports_path}"'/logcat_app_'"${type}"'.log\aClick here to check the app log.\e]8;;\a'
  echo ""
  echo ""
}

function clear_func() {
  echo "Killing pid of logcat: '${pid_logcat}'"
  kill -s SIGTERM "${pid_logcat}" 2> /dev/null

  local pid_app
  echo "Will kill MobileRT process"
  pid_app=$(adb shell ps | grep -i puscas.mobilertapp | tr -s ' ' | cut -d ' ' -f 2)
  echo "Killing pid of MobileRT: '${pid_app}'"
  adb shell kill -s SIGTERM "${pid_app}" 2> /dev/null

  # Kill all processes in the whole process group, thus killing also descendants.
  echo "All processes will be killed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  kill -9 -- -$$
}

function catch_signal() {
  echo ""
  echo ""
  echo "Caught signal"

  gather_logs_func
  # clear_func

  echo ""
  echo ""
}
###############################################################################
###############################################################################


###############################################################################
# Run Android tests in emulator
###############################################################################

function runEmulator() {
  echo "Prepare traps"
  trap 'catch_signal ${?} ${LINENO}' EXIT SIGHUP SIGINT SIGQUIT SIGILL SIGTRAP SIGABRT SIGTERM

  set +u;
  pid=${BASHPID}
  set -u;

  script_name=$(basename "${0}")
  echo "pid: ${pid}"
  echo "script name: ${script_name}"

  if [ "${type}" == "debug" ]; then
    code_coverage="createDebugCoverageReport"
  fi

  if [ "${kill_previous}" == true ]; then
    echo "Killing previous process"
    set +e
    callCommand ps aux |
      grep -v grep |
      grep -v "${pid}" |
      grep -i "${script_name}" |
      tr -s ' ' |
      cut -d ' ' -f 2 |
      xargs kill
    set -e
  fi

  if [ -x "$(command -v emulator)" ]; then
    avd_emulators=$(emulator -list-avds)
    echo "Emulators available: '${avd_emulators}'"
    avd_emulator=$(echo "${avd_emulators}" | head -1)
    echo "Start '${avd_emulator}'"
  else
    echo "Command emulator is NOT installed."
  fi
}

function waitForEmulator() {
  echo "Wait for device to be available."
  # Don't make the Android emulator belong in the process group, so it will not be killed at the end.
  callCommand set -m

  local adb_devices_running
  adb_devices_running=$(adb devices | tail -n +2)
  echo "Devices running: ${adb_devices_running}"
  if [ -z "${adb_devices_running}" ]; then
    callCommand cpulimit -- emulator -avd "${avd_emulator}" -writable-system 2> /dev/null &
  fi

  # Find at least 1 Android device on.
  callCommandUntilSuccess test -n "adb devices | tail -n +2 | head -1"

  # Make the all other processes belong in the process group, so that will be killed at the end.
  callCommand set +m

  echo "Wait for device to be ready to unlock."
  callCommand adb kill-server
  callCommandUntilSuccess adb start-server
  callCommandUntilSuccess adb wait-for-device
  # adb shell needs ' instead of ", so `getprop` works properly
  callCommandUntilSuccess adb shell 'while [[ $(getprop service.bootanim.exit) -ne 1 ]]; do sleep 1; done;'
  callCommandUntilSuccess adb shell whoami

  echo "Set adb as root, to be able to change files permissions"
  callCommandUntilSuccess adb root

  echo "Unlock device"
  callCommandUntilSuccess adb shell input tap 800 900
  callCommandUntilSuccess adb shell input keyevent 82

  # Abort if emulator didn't start
  local adb_devices_running
  adb_devices_running=$(adb devices | tail -n +2)
  echo "Devices running: ${adb_devices_running}"
  if [ -z "${adb_devices_running}" ]; then
    callCommand exit 1
  fi
}

function copyResources() {
  callCommand mkdir -p ${reports_path}

  echo "Prepare copy unit tests"
  adb shell mount -o remount,ro /
  adb shell mount -o remount,ro /mnt
  set +e;
  adb shell mount -o remount,rw /mnt/sdcard
  adb shell mount -o remount,rw /mnt/media_rw/1CE6-261B
  set -e;
  callCommand adb shell mkdir -p ${mobilert_path}
  callCommand adb shell mkdir -p ${sdcard_path}
  adb shell rm -r ${mobilert_path}
  adb shell rm -r ${sdcard_path}
  callCommand adb shell mkdir -p ${mobilert_path}
  callCommand adb shell mkdir -p ${sdcard_path}

  echo "Copy tests resources"
  callCommand adb push app/src/androidTest/resources/teapot ${mobilert_path}/WavefrontOBJs/teapot
  callCommand adb push app/src/androidTest/resources/CornellBox ${sdcard_path}/WavefrontOBJs/CornellBox

  echo "Copy and install File Manager"
  callCommand adb push app/src/androidTest/resources/APKs ${mobilert_path}/
  callCommand adb shell pm install -t -r "${mobilert_path}/APKs/com.asus.filemanager.apk"

  echo "Change resources permissions"
  callCommand adb shell chmod -R 777 ${mobilert_path}
  callCommand adb shell chmod -R 777 ${sdcard_path}
}

function startCopyingLogcatToFile() {
  # echo "Disable animations"
  # puscas.mobilertapp not found
  # callCommand adb shell pm grant puscas.mobilertapp android.permission.SET_ANIMATION_SCALE

  # /system/bin/sh: settings: not found
  # callCommand adb shell settings put global window_animation_scale 0.0
  # callCommand adb shell settings put global transition_animation_scale 0.0
  # callCommand adb shell settings put global animator_duration_scale 0.0

  echo "Activate JNI extended checking mode"
  callCommand adb shell setprop dalvik.vm.checkjni true
  callCommand adb shell setprop debug.checkjni 1

  echo "Clear logcat"
  callCommandUntilSuccess adb root
  callCommand adb shell logcat -b all -b main -b system -b radio -b events -b crash -c

  echo "Copy realtime logcat to file"
  callCommand adb logcat -v threadtime "*":V \
    2>&1 | tee ${reports_path}/logcat_current_"${type}".log &
  pid_logcat="$!"
  echo "pid of logcat: '${pid_logcat}'"
}

function runUnitTests() {
  echo "Copy unit tests"
  callCommand adb push app/build/intermediates/cmake/"${type}"/obj/x86/* ${mobilert_path}/

  echo "Run unit tests"
  if [ "${type}" == "debug" ]; then
    # Ignore unit tests that should crash the system because of a failing assert
    callCommand adb shell LD_LIBRARY_PATH=${mobilert_path} \
      ${mobilert_path}/UnitTests \
      --gtest_filter=-*.TestInvalid* \
      2>&1 | tee ${reports_path}/log_unit_tests_"${type}".log
  else
    callCommand adb shell LD_LIBRARY_PATH=${mobilert_path} \
      ${mobilert_path}/UnitTests \
      2>&1 | tee ${reports_path}/log_unit_tests_"${type}".log
  fi
  resUnitTests=${PIPESTATUS[0]}
}

function verifyResources() {
  echo "Verify resources in SD Card"
  callCommand adb shell ls -Rla ${mobilert_path}/WavefrontOBJs
  callCommand adb shell ls -Rla ${sdcard_path}/WavefrontOBJs
#  callCommand adb shell cat ${sdcard_path}/WavefrontOBJs/CornellBox/CornellBox-Water.obj
#  callCommand adb shell cat ${sdcard_path}/WavefrontOBJs/CornellBox/CornellBox-Water.mtl
#  callCommand adb shell cat ${sdcard_path}/WavefrontOBJs/CornellBox/CornellBox-Water.cam
}

function runInstrumentationTests() {
  echo "Run instrumentation tests"
  callCommand ./gradlew --stop
  if [ "${run_test}" == "all" ]; then
    echo "Running all tests"
    set +u; # Because `code_coverage` is only set when debug
    callCommand ./gradlew connected"${type}"AndroidTest -DtestType="${type}" \
      -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
      ${code_coverage} --console plain --parallel \
      2>&1 | tee ${reports_path}/log_tests_"${type}".log
    set -u;
  elif [[ ${run_test} == rep_* ]]; then
    run_test_without_prefix=${run_test#"rep_"}
    echo "Repeatable of test: ${run_test_without_prefix}"
    callCommandUntilError ./gradlew connected"${type}"AndroidTest -DtestType="${type}" \
      -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
      -Pandroid.testInstrumentationRunnerArguments.class="${run_test_without_prefix}" \
      --console plain --parallel \
      2>&1 | tee ${reports_path}/log_tests_"${type}".log
  else
    echo "Running test: ${run_test}"
    callCommand ./gradlew connected"${type}"AndroidTest -DtestType="${type}" \
      -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
      -Pandroid.testInstrumentationRunnerArguments.class="${run_test}" \
      --console plain --parallel \
      2>&1 | tee ${reports_path}/log_tests_"${type}".log
  fi
  resInstrumentationTests=${PIPESTATUS[0]}
  pid_instrumentation_tests="$!"
  echo "pid of instrumentation tests: '${pid_instrumentation_tests}'"
}
###############################################################################
###############################################################################

runEmulator
waitForEmulator
copyResources
startCopyingLogcatToFile
verifyResources
runInstrumentationTests
runUnitTests

###############################################################################
# Exit code
###############################################################################
printCommandExitCode "${resUnitTests}" "Unit tests"
printCommandExitCode "${resInstrumentationTests}" "Instrumentation tests"
###############################################################################
###############################################################################
