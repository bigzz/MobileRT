#!/bin/bash

###############################################################################
# Change directory to MobileRT root
###############################################################################
cd "$( dirname "${BASH_SOURCE[0]}" )/.."
###############################################################################
###############################################################################


###############################################################################
# Get arguments
###############################################################################
type="${1:-Release}"
ndk_version="${2:-21.3.6528147}"
cmake_version="${3:-3.10.2}"
###############################################################################
###############################################################################


###############################################################################
# Get helper functions
###############################################################################
source scripts/helper_functions.sh;
###############################################################################
###############################################################################


###############################################################################
# Compile for Android
###############################################################################

# Capitalize 1st letter
type="$(tr '[:lower:]' '[:upper:]' <<< "${type:0:1}")${type:1}";
echo "type: '${type}'";

# Set path to reports
reports_path=./app/build/reports
callCommand mkdir -p ${reports_path}

rm -rf ./app/.cxx/;
rm -rf ./app/build/;

files_being_used=$(find . -name "*.fuse_hidden*" | grep -i ".fuse_hidden")
echo "files_being_used: '${files_being_used}'";

if [ "${files_being_used}" != "" ]; then
  processes_using_files=$(lsof "${files_being_used}" | tail -n +2 | tr -s ' ');
  echo "processes_using_files: '${processes_using_files}'";
  processes_id_using_files=$(echo "${processes_using_files}" | cut -d ' ' -f 2 | head -1);
  echo "Going to kill this process: '${processes_id_using_files}'";
  kill "${processes_id_using_files}";
  while [ -f "${files_being_used}" ]; do
    echo "sleeping 1 sec";
    sleep 1
  done
fi

callCommand rm -rf ./app/.cxx/;
callCommand rm -rf ./app/build/;

callCommand ./gradlew clean assemble"${type}" --profile --parallel \
  -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
  --console plain \
  | tee ${reports_path}/log_build_"${type}".log 2>&1;
resCompile=${PIPESTATUS[0]};
###############################################################################
###############################################################################


###############################################################################
# Exit code
###############################################################################
echo "########################################################################"
echo "Results:"
if [ "${resCompile}" -eq 0 ]; then
  echo "Compilation: success"
else
  echo "Compilation: failed"
  exit "${resCompile}"
fi
###############################################################################
###############################################################################
