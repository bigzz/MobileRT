#!/bin/bash

###############################################################################
# Change directory to MobileRT root
###############################################################################
cd "$(dirname "${BASH_SOURCE[0]}")/.." || exit
###############################################################################
###############################################################################

###############################################################################
# Get arguments
###############################################################################
type="${1:-Release}"
compiler="${2:-g++}"
recompile="${3:-no}"
###############################################################################
###############################################################################

###############################################################################
# Get helper functions
###############################################################################
source scripts/helper_functions.sh
###############################################################################
###############################################################################


###############################################################################
# Fix llvm clang OpenMP library
###############################################################################
OPENMP_INCLUDE_PATH="/usr/local/Cellar/libomp/11.0.1/include";
OPENMP_LIB_PATH=$(find /usr -name "libomp.so" -path "*llvm*" | head -1 2> /dev/null);
echo "OPENMP_INCLUDE_PATH = ${OPENMP_INCLUDE_PATH}";
echo "OPENMP_LIB_PATH = ${OPENMP_LIB_PATH}";
export CPLUS_INCLUDE_PATH=${OPENMP_INCLUDE_PATH%/omp.h}:${CPLUS_INCLUDE_PATH};
export LIBRARY_PATH=${OPENMP_LIB_PATH%/libomp.so}:${LIBRARY_PATH};
###############################################################################
###############################################################################


###############################################################################
# Get the proper C compiler for conan
# Possible values for clang are ['3.3', '3.4', '3.5', '3.6', '3.7', '3.8', '3.9', '4.0',
# '5.0', '6.0', '7.0', '7.1', '8', '9', '10', '11']
# Possible values for gcc (Apple clang) are ['4.1', '4.4', '4.5', '4.6', '4.7', '4.8',
# '4.9', '5', '5.1', '5.2', '5.3', '5.4', '5.5', '6', '6.1', '6.2', '6.3', '6.4',
# '6.5', '7', '7.1', '7.2', '7.3', '7.4', '7.5', '8', '8.1', '8.2', '8.3', '8.4',
# '9', '9.1', '9.2', '9.3', '10', '10.1']
###############################################################################
if [[ "${compiler}" == *"clang++"* ]]; then
  conan_compiler="clang";
  conan_compiler_version=$(${compiler} --version | grep -i version | tr -s ' ' | cut -d ' ' -f 3 | cut -d '-' -f 1 | cut -d '.' -f1,2);
  export CC=clang;
elif [[ "${compiler}" == *"g++"* ]]; then
  conan_compiler="gcc";
  conan_compiler_version=$(${compiler} -dumpversion | cut -d '.' -f 1,2);
  export CC=gcc;
fi

callCommand ${compiler} -v;
echo "Detected '${conan_compiler}' '${conan_compiler_version}' compiler.";
export CXX="${compiler}";

# Fix compiler version used
#if [ "${conan_compiler_version}" == "9.0" ]; then
#  conan_compiler_version=9
#fi
#if [ "${conan_compiler_version}" == "12.0" ]; then
#  conan_compiler_version=12
#fi
#if [ "${conan_compiler_version}" == "4.2" ]; then
#  conan_compiler_version=4.0
#fi
###############################################################################
###############################################################################

###############################################################################
# Get Conan path
###############################################################################
if [ ! -x "$(command -v conan)" ]; then
  CONAN_PATH=$(find ~/ -name "conan" || true);
fi
echo "Conan binary: ${CONAN_PATH}"
echo "Conan location: ${CONAN_PATH%/conan}"
if [ -n "${CONAN_PATH}" ]; then
  PATH=${CONAN_PATH%/conan}:${PATH}
fi
echo "PATH: ${PATH}"
###############################################################################
###############################################################################

###############################################################################
# Get CPU Architecture
###############################################################################
CPU_ARCHITECTURE=x86_64
if [ -x "$(command -v uname)" ]; then
  CPU_ARCHITECTURE=$(uname -m)
  if [ "${CPU_ARCHITECTURE}" == "aarch64" ]; then
    CPU_ARCHITECTURE=armv8
  fi
fi
###############################################################################
###############################################################################

###############################################################################
# Add Conan remote dependencies
###############################################################################
# Install C++ Conan dependencies
function install_conan_dependencies() {
#  ln -s configure/config.guess /home/travis/.conan/data/libuuid/1.0.3/_/_/build/b818fa1fc0d3879f99937e93c6227da2690810fe/configure/config.guess
#  ln -s configure/config.sub /home/travis/.conan/data/libuuid/1.0.3/_/_/build/b818fa1fc0d3879f99937e93c6227da2690810fe/configure/config.sub

  if [ -x "$(command -v conan)" ]; then
    conan profile new default;
    callCommand conan profile update settings.compiler="${conan_compiler}" default;
    callCommand conan profile update settings.compiler.version="${conan_compiler_version}" default;
    # Possible values for compiler.libcxx are ['libstdc++', 'libstdc++11']
    callCommand conan profile update settings.compiler.libcxx="libstdc++11" default;
    callCommand conan profile update settings.arch="${CPU_ARCHITECTURE}" default;
    callCommand conan profile update settings.os="Linux" default;
    callCommand conan profile update settings.build_type="Release" default;
    conan remote add bintray https://api.bintray.com/conan/bincrafters/public-conan;
  fi

  callCommand conan install \
  -s compiler=${conan_compiler} \
  -s compiler.version="${conan_compiler_version}" \
  -s compiler.libcxx=libstdc++11 \
  -s arch="${CPU_ARCHITECTURE}" \
  -s os="Linux" \
  -s build_type=Release \
  -o bzip2:shared=True \
  --build missing \
  --profile default \
  ../app/third_party/conan/Native

  export CONAN="TRUE"
}
###############################################################################
###############################################################################

###############################################################################
# Compile for native
###############################################################################

# Capitalize 1st letter
type="$(tr '[:lower:]' '[:upper:]' <<<"${type:0:1}")${type:1}"
echo "type: '${type}'"

# Set path to build
build_path=./build_${type}

if [ "${recompile}" == "yes" ]; then
  callCommand rm -rf "${build_path}"/*
fi
callCommand mkdir -p "${build_path}"

function build() {
  callCommand cd "${build_path}"
#  install_conan_dependencies

  echo "Calling CMake"
  callCommand cmake -DCMAKE_VERBOSE_MAKEFILE=ON \
    -DCMAKE_CXX_COMPILER="${compiler}" \
    -DCMAKE_BUILD_TYPE="${type}" \
    ../app/ \
    2>&1 | tee ./log_cmake_"${type}".log
  resCompile=${PIPESTATUS[0]}

  if [ "${resCompile}" -eq 0 ]; then
    echo "Calling Make"
    callCommand cmake --build . 2>&1 | tee ./log_make_"${type}".log
    resCompile=${PIPESTATUS[0]}
  else
    echo "Compilation: cmake failed"
  fi
}
###############################################################################
###############################################################################

build

###############################################################################
# Exit code
###############################################################################
printCommandExitCode "${resCompile}" "Compilation"
###############################################################################
###############################################################################
