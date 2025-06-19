#!/bin/bash

MACHINE=sparrow-hawk
KERNEL_VERSION=6.12

SCRIPT_DIR=$(cd `dirname $0` && pwd)

mkdir -p ${SCRIPT_DIR}/build
cd ${SCRIPT_DIR}/build
export WORK=`pwd`
TARGET_IMAGE=core-image-minimal
USE_GPU=no
IS_BUILD_INSIDE_REPO=yes

Usage () {
    echo "Usage:"
    echo "    $0 <image_option> <options>"
    echo "image option:"
    echo "    --console:      Use CLI(default)"
    echo "    --weston-nogpu: Use GUI, but no graphics accelaration"
    echo "options:"
    echo "    -h | --help:    Show this help"
    exit
}
for arg in $@; do
    if [[ "$arg" == "--weston-nogpu" ]]; then
        echo "weston(nogpu) image is seletected"
        TARGET_IMAGE=core-image-weston
    elif [[ "$arg" == "-h" ]] || [[ "$arg" == "--help" ]]; then
        Usage; exit
    fi
done

if [[ "$(echo $0 | grep '\.sh' )" != "" ]] ;then
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
    git clone https://github.com/renesas-rcar/meta-renesas-upstream-bsp.git
fi

git -C poky checkout -b scarthgap origin/scarthgap
git -C meta-openembedded checkout -b scarthgap origin/scarthgap
if [[ "${IS_BUILD_INSIDE_REPO}" == "no" ]]; then
    git -C meta-renesas-upstream-bsp checkout -b scarthgap origin/scarthgap
fi

cd $WORK
rm -rf build-$MACHINE/conf
TEMPLATECONF=${SCRIPT_DIR}/conf/templates/$MACHINE  . poky/oe-init-build-env build-$MACHINE

if [[ "${MACHINE}" == "sparrow-hawk" ]]; then
    FIRMWARE_LIST=("rcar_gen4_pcie.bin" "renesas_usb_fw.mem")
    for item in ${FIRMWARE_LIST[@]}; do
        if [[ ! -e ${SCRIPT_DIR}/firmware/${item} ]]; then
            echo "${SCRIPT_DIR}/firmware/${item} is not found !!"
            echo "Dummy file is created: ${SCRIPT_DIR}/firmware/${item}"
            touch ${SCRIPT_DIR}/firmware/${item}
        fi
    done
fi

cat << EOS >> conf/local.conf
BB_HASHSERVE_UPSTREAM = "wss://hashserv.yoctoproject.org/ws"
SSTATE_MIRRORS ?= "file://.* http://cdn.jsdelivr.net/yocto/sstate/all/PATH;downloadfilename=PATH"
BB_HASHSERVE = "auto"
BB_SIGNATURE_HANDLER = "OEEquivHash"
EOS

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

