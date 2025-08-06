#!/bin/bash

MACHINE=sparrow-hawk
KERNEL_VERSION=6.12

SCRIPT_DIR=$(cd `dirname $0` && pwd)

mkdir -p ${SCRIPT_DIR}/build
cd ${SCRIPT_DIR}/build
export WORK=`pwd`
TARGET_IMAGE=core-image-minimal
USE_GPU=no
USE_SSTATE_MIRROR=no
BUILD_SBOM=no
IS_BUILD_INSIDE_REPO=yes
IS_BUILD_SDK=no

Usage () {
    echo "Usage:"
    echo "    $0 <image_option> <options>"
    echo "image option:"
    echo "    --console:      Use CLI(default)"
    echo "    --weston-nogpu: Use GUI, but no graphics accelaration"
    echo "options:"
    echo "    -h | --help:          Show this help"
    echo "    -s | --sdk:           Build Yocto SDK"
    echo "       | --sbom:          Build SBOM files"
    echo "       | --sstate-mirror: Use sstate mirror server. This may decrease build time."
    exit
}
for arg in $@; do
    if [[ "$arg" == "--weston-nogpu" ]]; then
        echo "weston(nogpu) image is seletected"
        TARGET_IMAGE=core-image-weston
    elif [[ "$arg" == "-h" ]] || [[ "$arg" == "--help" ]]; then
        Usage; exit
    elif [[ "$arg" == "-s" ]] || [[ "$arg" == "--sdk" ]]; then
        IS_BUILD_SDK=yes
    elif [[ "$arg" == "--sbom" ]]; then
        BUILD_SBOM=yes
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

git -C poky checkout -b scarthgap 6f7e929ea6ea557f107c8ccffea69a7d73439591
git -C meta-openembedded checkout -b scarthgap e8fd97d86af86cdcc5a6eb3f301cbaf6a2084943
if [[ "${IS_BUILD_INSIDE_REPO}" == "no" ]]; then
    git -C meta-sparrow-hawk checkout -b scarthgap e514f1da11336ea2a830fe8108655bb7eb7433a3
fi

cd $WORK
rm -rf build-$MACHINE/conf
TEMPLATECONF=${WORK}/meta-sparrow-hawk/conf/templates/$MACHINE  . poky/oe-init-build-env build-$MACHINE

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
fi

if [[ "$TARGET_IMAGE" == "core-image-weston" ]]; then
cat << EOS >> conf/local.conf
IMAGE_INSTALL:append = " mesa glmark2"
DISTRO_FEATURES_NATIVESDK:append = " wayland"
DISTRO_FEATURES:append = " pam"
IMAGE_INSTALL:append = " glmark2 kernel-devicetree"
DISTRO_FEATURES:remove = " ptest x11 vulkan"
EOS
fi
if [[ "$USE_GPU" == "yes" ]]; then
   echo "Not implemented now"
fi

bitbake ${TARGET_IMAGE}
if [[ "${IS_BUILD_SDK}" == "yes" ]]; then
    bitbake ${TARGET_IMAGE} -c populate_sdk
fi

