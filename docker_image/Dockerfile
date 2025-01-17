###############################################################################
# README
###############################################################################
# This Dockerfile prepares a docker image with MobileRT.
#
# It setups MobileRT by fetching it from git and compiling it using the
# `install_dependencies.sh` and `compile_native.sh` scripts inside the docker
# image.
# The script `install_dependencies.sh` installs all the Linux necessary
# dependencies in order to compile MobileRT.
# And the script `compile_native.sh` compiles the MobileRT.
#
# At the moment, this Dockerfile allows to setup MobileRT inside a docker image
# in multiple Linux distributions:
# * Debian
# * Red Hat
# * Arch
# * Alpine
# * Gentoo
###############################################################################
###############################################################################


###############################################################################
# Set base docker image
###############################################################################
ARG BASE_IMAGE=ubuntu:22.04
FROM ${BASE_IMAGE}
###############################################################################
###############################################################################


###############################################################################
# Setup arguments
###############################################################################
ARG BUILD_TYPE=release
ARG BRANCH=master
###############################################################################
###############################################################################


###############################################################################
# Change user to root
###############################################################################
USER root
###############################################################################
###############################################################################


###############################################################################
# Prepare environment
###############################################################################
ENV \
    # Create an environment variable to use it in the docker container when boot
    ENV_BUILD_TYPE=${BUILD_TYPE} \
    # Allow execution of Qt in docker
    DISPLAY=":1" \
    QT_GRAPHICSSYSTEM="native" \
    # Set lib path
    LD_LIBRARY_PATH="/MobileRT/libraries:/MobileRT/build_${BUILD_TYPE}/lib" \
    # Set apt-get as non interactive mode
    DEBIAN_FRONTEND="noninteractive"
###############################################################################
###############################################################################


###############################################################################
# Print environment
###############################################################################
RUN echo "DISPLAY = ${DISPLAY}"; \
    echo "QT_GRAPHICSSYSTEM = ${QT_GRAPHICSSYSTEM}"; \
    echo "LD_LIBRARY_PATH = ${LD_LIBRARY_PATH}"; \
    echo "DEBIAN_FRONTEND = ${DEBIAN_FRONTEND}"; \
    echo "BUILD_TYPE = ${BUILD_TYPE}";
###############################################################################
###############################################################################


###############################################################################
# Install git
###############################################################################
RUN \
if [ -x "$(command -v apt-get)" ]; then \
  echo 'Detected Debian based Linux'; \
  apt-get update -y; \
  apt-get install --no-install-recommends -y sudo git tzdata; \
elif [ -x "$(command -v yum)" ]; then \
  echo 'Detected Red Hat based Linux'; \
  yum --setopt=skip_missing_names_on_install=False --setopt=skip_missing_names_on_update=False update -y; \
  yum --setopt=skip_missing_names_on_install=False --setopt=skip_missing_names_on_update=False install -y \
    sudo git; \
elif [ -x "$(command -v pacman)" ]; then \
  echo 'Detected Arch based Linux'; \
  rm -rf /var/cache/pacman/pkg/; \
  pacman -Sy archlinux-keyring --noconfirm --needed; \
  echo 'Resetting all the keys'; \
  pacman-key --init; \
  pacman-key --populate; \
  echo 'Upgrade system'; \
  pacman -Syu --noconfirm --needed; \
  pacman -Sy --noconfirm --needed sudo git; \
elif [ -x "$(command -v apk)" ]; then \
  echo 'Detected Alpine based Linux'; \
  apk update; \
  apk add sudo git; \
elif [ -x "$(command -v emerge)" ]; then \
  echo 'Detected Gentoo based Linux'; \
  emerge --sync || true; \
  emerge sudo dev-vcs/git; \
else \
  echo 'Detected unknown based Linux'; \
fi
###############################################################################
###############################################################################


###############################################################################
# Configure git
###############################################################################
RUN git config --global http.postBuffer 1048576000; \
    git config --global https.postBuffer 1048576000; \
    git config --global core.compression -1; \
    git config --global http.sslVerify "false";
###############################################################################
###############################################################################


###############################################################################
# Get MobileRT contents (required to create docker image in CI)
###############################################################################
RUN git clone https://github.com/TiagoMSSantos/MobileRT.git \
  --shallow-submodules \
  --progress \
  --branch ${BRANCH};
###############################################################################
###############################################################################


###############################################################################
# Alernative: Copy MobileRT contents locally (not possible in CI)
###############################################################################
# COPY . MobileRT
###############################################################################
###############################################################################


###############################################################################
# Install dependencies
###############################################################################
WORKDIR MobileRT
RUN sh scripts/install_dependencies.sh;
###############################################################################
###############################################################################


###############################################################################
# Compile MobileRT
###############################################################################
RUN sh scripts/compile_native.sh -t ${BUILD_TYPE} -c g++ -r yes && \
    du -h -d 1 build_${BUILD_TYPE}/bin && \
    du -h -d 1 build_${BUILD_TYPE}/lib && \
    rm -rf app && \
    rm -rf docs && \
    rm -rf documentation && \
    rm -rf git-hooks && \
    rm -rf gradle && \
    rm -rf .git
###############################################################################
###############################################################################


###############################################################################
# Create volume (for Qt)
###############################################################################
VOLUME /tmp/.X11-unix
###############################################################################
###############################################################################


###############################################################################
# Execute MobileRT by default
###############################################################################
WORKDIR build_${BUILD_TYPE}
ENTRYPOINT ["sh", "-c"]
CMD ["sh ../scripts/profile.sh ${ENV_BUILD_TYPE}"]
###############################################################################
###############################################################################
