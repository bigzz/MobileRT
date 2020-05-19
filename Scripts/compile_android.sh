#!/bin/bash

###############################################################################
# Get arguments
###############################################################################
type="${1:-Release}"
ndk_version="${2:-21.0.6113669}"
cmake_version="${3:-3.6.0}"
###############################################################################
###############################################################################


###############################################################################
# Get helper functions
###############################################################################
source Scripts/helper_functions.sh;
###############################################################################
###############################################################################


###############################################################################
# Compile for Android
###############################################################################

# Set path to reports
reports_path=./app/build/reports
callCommand mkdir -p ${reports_path}
callCommand find -name "*.fuse_hidden*" | grep -i ".fuse_hidden" | xargs lsof \
  | cut -d ' ' -f 5 | xargs kill
callCommand rm -rf ./app/build/

callCommand ./gradlew clean assemble${type} --profile --parallel \
  -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
  | tee ${reports_path}/log_build_${type}.log 2>&1;
resCompile=${PIPESTATUS[0]};
###############################################################################
###############################################################################


###############################################################################
# Exit code
###############################################################################
echo "########################################################################"
echo "Results:"
if [ ${resCompile} -eq 0 ]; then
  echo "Compilation: success"
else
  echo "Compilation: failed"
  exit ${resCompile}
fi
###############################################################################
###############################################################################
