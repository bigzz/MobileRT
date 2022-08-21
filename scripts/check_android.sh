#!/usr/bin/env bash

###############################################################################
# README
###############################################################################
# This script runs the gradle linter in the codebase.
###############################################################################
###############################################################################


###############################################################################
# Exit immediately if a command exits with a non-zero status.
###############################################################################
set -euo pipefail;
###############################################################################
###############################################################################


###############################################################################
# Change directory to MobileRT root.
###############################################################################
cd "$(dirname "${BASH_SOURCE[0]}")/.." || exit;
###############################################################################
###############################################################################


###############################################################################
# Get helper functions.
###############################################################################
# shellcheck disable=SC1091
. scripts/helper_functions.sh;
###############################################################################
###############################################################################


###############################################################################
# Execute Shellcheck on this script.
###############################################################################
if [ -x "$(command -v shellcheck)" ]; then
  shellcheck "${0}" || exit
fi
###############################################################################
###############################################################################


###############################################################################
# Set default arguments.
###############################################################################
ndk_version="23.2.8568313";
cmake_version="3.18.1";
cpu_architecture="x86";
parallelizeBuild;

function printEnvironment() {
  echo "";
  echo "Selected arguments:";
  echo "ndk_version: ${ndk_version}";
  echo "cmake_version: ${cmake_version}";
  echo "cpu_architecture: ${cpu_architecture}";
}
###############################################################################
###############################################################################


###############################################################################
# Parse arguments.
###############################################################################
parseArgumentsToCheck "$@";
printEnvironment;
###############################################################################
###############################################################################


###############################################################################
# Run Gradle linter.
###############################################################################
function runLinter() {
  # Set path to reports.
  echo "Print Gradle version";
  bash gradlew --no-rebuild --stop;
  bash gradlew --no-rebuild --version;

  echo "Calling the Gradle linter";
  bash gradlew lint --profile --parallel \
    -DndkVersion="${ndk_version}" -DcmakeVersion="${cmake_version}" \
    -DabiFilters="[${cpu_architecture}]" \
    --no-rebuild \
    --console plain;
  resCheck=${PIPESTATUS[0]};
}
###############################################################################
###############################################################################

createReportsFolders;
runLinter;

###############################################################################
# Exit code
###############################################################################
printCommandExitCode "${resCheck}" "Check";
###############################################################################
###############################################################################
