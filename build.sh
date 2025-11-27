#!/bin/bash

MACHINE=sparrow-hawk

SCRIPT_DIR=$(cd `dirname $0` && pwd)

mkdir -p ${SCRIPT_DIR}/build
cd ${SCRIPT_DIR}/build
export WORK=`pwd`
TARGET_IMAGE=core-image-minimal
USE_SSTATE_MIRROR=no
BUILD_SBOM=no
REMOVE_WORKDIR=no
IS_BUILD_INSIDE_REPO=yes
IS_BUILD_SDK=no
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
    echo "       | --sbom:          Build SBOM files"
    echo "       | --sstate-mirror: Use sstate mirror server. This may decrease build time."
    echo "       | --rm-work:       Remove working directory while building to reduce storage space."
    exit
}
for arg in $@; do
    if [[ "$arg" == "--console" ]]; then
        TEMPLATE_POSTFIX=""
        TARGET_IMAGE=core-image-minimal
    elif [[ "$arg" == "--weston" ]]; then
        TEMPLATE_POSTFIX="-weston"
        TARGET_IMAGE=core-image-weston
    elif [[ "$arg" == "-h" ]] || [[ "$arg" == "--help" ]]; then
        Usage; exit
    elif [[ "$arg" == "-s" ]] || [[ "$arg" == "--sdk" ]]; then
        IS_BUILD_SDK=yes
    elif [[ "$arg" == "--sbom" ]]; then
        BUILD_SBOM=yes
    elif [[ "$arg" == "--rm-work" ]]; then
        REMOVE_WORKDIR=yes
    elif [[ "$arg" == "--sstate-mirror" ]]; then
        USE_SSTATE_MIRROR=yes
    fi
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

git -C poky checkout -B scarthgap 7e8674996b0164b07e56bc066d0fba790e627061
git -C meta-openembedded checkout -B scarthgap b79cf94b4d53d75ddc4b4f468c1b1ad42a3fffe2
if [[ "${IS_BUILD_INSIDE_REPO}" == "no" ]]; then
    git -C meta-sparrow-hawk checkout -B scarthgap v2025-11-28
fi

cd $WORK
rm -rf build-$MACHINE/conf
TEMPLATECONF=${WORK}/meta-sparrow-hawk/conf/templates/$MACHINE${TEMPLATE_POSTFIX} \
    . poky/oe-init-build-env build-$MACHINE

if [[ "${MACHINE}" == "sparrow-hawk" ]]; then
    FIRMWARE_LIST=("rcar_gen4_pcie.bin")
    for item in ${FIRMWARE_LIST[@]}; do
        if [[ ! -e ${WORK}/meta-sparrow-hawk/firmware/${item} ]]; then
            echo "${WORK}/meta-sparrow-hawk/firmware/${item} is not found !!"
            echo "Dummy file is created: ${WORK}/meta-sparrow-hawk/firmware/${item}"
            mkdir -p ${WORK}/meta-sparrow-hawk/firmware/
            touch ${WORK}/meta-sparrow-hawk/firmware/${item}
        fi
    done
fi

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

if [[ "${REMOVE_WORKDIR}" == "yes" ]]; then
    echo 'INHERIT += "rm_work"' >> conf/local.conf
fi

bitbake ${TARGET_IMAGE}
if [[ "${IS_BUILD_SDK}" == "yes" ]]; then
    bitbake ${TARGET_IMAGE} -c populate_sdk
fi

# Cleanup symbolic link
if [[ "${IS_BUILD_INSIDE_REPO}" == "yes" ]]; then
    rm -f ${WORK}/meta-sparrow-hawk
fi

