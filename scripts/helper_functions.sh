#!/bin/bash

###############################################################################
# Helper functions
###############################################################################

# Call function and exit the process if it fails
function callCommand() {
  echo "";
  echo "Calling '$*'";
  "$@";
  local lastResult=${PIPESTATUS[0]};
  local lastCommand="$*";
  if [ "${lastResult}" -eq 0 ]; then
    echo "${lastCommand}: success - '${lastResult}'";
  else
    echo "${lastCommand}: failed - '${lastResult}'";
    echo "";
    exit "${lastResult}";
  fi
}

# Call function multiple times until it fails and exit the process
function callCommandUntilError() {
  echo "";
  echo "Calling until error '$*'";
  local retry=0;
  "$@";
  local lastResult=${PIPESTATUS[0]};
  while [ "${lastResult}" -eq 0 ]; do
    echo "Retry: ${retry}";
    retry=$(( "${retry}" + 1 ));
    "$@";
    lastResult=${PIPESTATUS[0]};
  done
  exit "${lastResult}";
}
###############################################################################
###############################################################################
