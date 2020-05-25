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
# Run unit tests natively
###############################################################################

# Set path to reports
reports_path=./app/build/reports
callCommand mkdir -p ${reports_path}

callCommand ./gradlew test${type}UnitTest --profile --parallel \
  -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
  | tee ${reports_path}/log_native_tests_${type}.log 2>&1;
resUnitTests=${PIPESTATUS[0]};
###############################################################################
###############################################################################


###############################################################################
# Exit code
###############################################################################
echo "########################################################################"
echo "Results:"
if [ ${resUnitTests} -eq 0 ]; then
  echo "Unit tests: success"
else
  echo "Unit tests: failed"
  exit ${resUnitTests}
fi
###############################################################################
###############################################################################