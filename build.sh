#!/bin/bash

MACHINE=sparrow-hawk

SCRIPT_DIR=$(cd `dirname $0` && pwd)

mkdir -p ${SCRIPT_DIR}/build
cd ${SCRIPT_DIR}/build
export WORK=`pwd`
TARGET_IMAGE=core-image-minimal
USE_SSTATE_MIRROR=no
BUILD_SBOM=no
BUILD_ROOTFS_ONLY=no
REMOVE_WORKDIR=no
IS_BUILD_INSIDE_REPO=yes
IS_BUILD_SDK=no
IS_QUIET_BUILD=no
TEMPLATE_POSTFIX=""

Usage () {
    echo "Usage:"
    echo "    $0 <image_option> <options>"
    echo "image option:"
    echo "    --console:      Use CLI(default)"
    echo "    --weston:       Use GUI"
    echo "options:"
    echo "    -h | --help:          Show this help"
    echo "    -s | --sdk:           Build Yocto SDK"
    echo "    -q | --quiet:         Pass -q option to bitbake command"
    echo "       | --sbom:          Build SBOM files"
    echo "       | --sstate-mirror: Use sstate mirror server. This may decrease build time."
    echo "       | --rm-work:       Remove working directory while building to reduce storage space."
    exit
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --console)
            TEMPLATE_POSTFIX=""
            TARGET_IMAGE=core-image-minimal ;;
        --weston)
            TEMPLATE_POSTFIX="-weston"
            TARGET_IMAGE=core-image-weston ;;
        -h|--help)
            Usage; exit 0 ;;
        -s|--sdk)
            IS_BUILD_SDK=yes  ;;
        -q|--quiet)
            IS_QUIET_BUILD=yes ;;
        --sbom)
            BUILD_SBOM=yes ;;
        --rm-work)
            REMOVE_WORKDIR=yes ;;
        --sstate-mirror)
            USE_SSTATE_MIRROR=yes ;;
        --build-rootfs-only)
            BUILD_ROOTFS_ONLY=yes ;;
        *) ;; # Ignore unknown option
    esac
    shift
done

if [[ "$(cat ${SCRIPT_DIR}/conf/layer.conf 2>&1 | grep LAYERDEPENDS_sparrow-hawk )" != "" ]] ;then
    IS_BUILD_INSIDE_REPO=yes
else
    IS_BUILD_INSIDE_REPO=no
fi

cd $WORK
git clone git://git.yoctoproject.org/poky
git clone git://git.openembedded.org/meta-openembedded
if [[ "${IS_BUILD_INSIDE_REPO}" == "yes" ]]; then
    rm -f meta-sparrow-hawk
    ln -sfd ${SCRIPT_DIR} meta-sparrow-hawk
else
    git clone https://github.com/rcar-community/meta-sparrow-hawk.git
fi

git -C poky checkout -B scarthgap da5493bf86b3e75bbae4c5789fdfaca67b6f6a65
git -C meta-openembedded checkout -B scarthgap 06f846a325fde423bb0a6d49d771d8c1e144d7eb
if [[ "${IS_BUILD_INSIDE_REPO}" == "no" ]]; then
    git -C meta-sparrow-hawk checkout -B scarthgap v2026-04-24
fi

cd $WORK
rm -rf build-$MACHINE/conf
TEMPLATECONF=${WORK}/meta-sparrow-hawk/conf/templates/$MACHINE${TEMPLATE_POSTFIX} \
    . poky/oe-init-build-env build-$MACHINE

if [[ "${USE_SSTATE_MIRROR}" == "yes" ]]; then
cat << EOS >> conf/local.conf
BB_HASHSERVE_UPSTREAM = "wss://hashserv.yoctoproject.org/ws"
SSTATE_MIRRORS ?= "file://.* http://cdn.jsdelivr.net/yocto/sstate/all/PATH;downloadfilename=PATH"
BB_HASHSERVE = "auto"
BB_SIGNATURE_HANDLER = "OEEquivHash"
EOS
fi

if [[ "${BUILD_SBOM}" == "yes" ]]; then
cat << EOS >> conf/local.conf
# added for SBOM
# required. enable to generate spdx files.
INHERIT += "create-spdx"

# optional. if "1", output spdx files will be formatted.
SPDX_PRETTY = "1"

# optional. if "1", output spdx files includes [file-information section](https://spdx.github.io/spdx-spec/v2.3/file-information/).
SPDX_INCLUDE_SOURCES = "1"

# optional. if "1", bitbake will create source files archive for each package.
SPDX_ARCHIVE_SOURCES = "1"

# optional. if "1", bitbake will create output binary archive for each package.
SPDX_ARCHIVE_PACKAGED = "1"
EOS
else # Disable SBOM build to reduce build time
cat << EOS >> conf/local.conf
# Disable create-spdx
INHERIT:remove = "create-spdx"
EOS
fi

if [[ "${BUILD_ROOTFS_ONLY}" == "yes" ]]; then
cat << EOS >> conf/local.conf
# Disable kernel building
PREFERRED_PROVIDER_virtual/kernel = "linux-dummy"
EOS
fi

if [[ "${REMOVE_WORKDIR}" == "yes" ]]; then
    echo 'INHERIT += "rm_work"' >> conf/local.conf
fi

QUIET_FLAG=""
if [[ "${IS_QUIET_BUILD}" == "yes" ]]; then
    QUIET_FLAG="-q"
fi

bitbake ${QUIET_FLAG} ${TARGET_IMAGE}
if [[ "${IS_BUILD_SDK}" == "yes" ]]; then
    bitbake ${QUIET_FLAG} ${TARGET_IMAGE}-sdk -c populate_sdk
fi

# Cleanup symbolic link
if [[ "${IS_BUILD_INSIDE_REPO}" == "yes" ]]; then
    rm -f ${WORK}/meta-sparrow-hawk
fi

