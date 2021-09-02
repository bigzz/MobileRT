#!/bin/bash

###############################################################################
# README
###############################################################################
# This script runs the gradle linter in the codebase.
###############################################################################
###############################################################################


###############################################################################
# Exit immediately if a command exits with a non-zero status
###############################################################################
set -euo pipefail;
###############################################################################
###############################################################################


###############################################################################
# Change directory to MobileRT root
###############################################################################
cd "$(dirname "${BASH_SOURCE[0]}")/.." || exit
###############################################################################
###############################################################################


###############################################################################
# Get helper functions
###############################################################################
source scripts/helper_functions.sh
###############################################################################
###############################################################################


###############################################################################
# Set default arguments
###############################################################################
ndk_version="21.3.6528147";
cmake_version="3.10.2";
parallelizeBuild;

function printEnvironment() {
  echo "";
  echo "Selected arguments:";
  echo "ndk_version: ${ndk_version}";
  echo "cmake_version: ${cmake_version}";
}
###############################################################################
###############################################################################


###############################################################################
# Parse arguments
###############################################################################
parseArgumentsToCheck "$@";
printEnvironment;
###############################################################################
###############################################################################


###############################################################################
# Run Gradle linter
###############################################################################
function runLinter() {
  # Set path to reports
  reports_path=./app/build/reports
  profile_path=./reports/profile
  mkdir -p ${reports_path}
  mkdir -p ${profile_path}

  echo "Print Gradle version"
  ./gradlew --stop
  ./gradlew --version

  echo "Calling the Gradle linter"
  ./gradlew check --profile --parallel \
    -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
    --console plain \
    2>&1 | tee ${reports_path}/log_check.log
  resCheck=${PIPESTATUS[0]}
}
###############################################################################
###############################################################################

runLinter

###############################################################################
# Exit code
###############################################################################
printCommandExitCode "${resCheck}" "Check"
###############################################################################
###############################################################################
