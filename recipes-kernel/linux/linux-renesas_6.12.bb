SUMMARY = "Linux kernel v6.12"
DESCRIPTION = "Linux kernel v6.12 for the R-Car board"
HOMEPAGE = "https://github.com/rcar-community/linux"
BUGTRACKER = "https://github.com/orgs/rcar-community/discussions/categories/q-a"
SECTION = "kernel"
LICENSE = "GPLv2-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

# nooelint: oelint.file.requirenotfound - This file is provided by poky
require recipes-kernel/linux/linux-yocto.inc
CVE_PRODUCT ?= ""

# LINUX_VERSION/REPO/BRANCH/SRCREV are defined in inc file
require recipes-kernel/linux/kernel_6.12.inc
COMPATIBLE_MACHINE = "(rcar-gen3|rcar-gen4)"

# nooelint: oelint.vars.mispell.unknown - Yocto variable
KCONFIG_MODE = "alldefconfig"
# nooelint: oelint.vars.mispell.unknown - Use in tree defconfig
KBUILD_DEFCONFIG:sparrow-hawk = "renesas_defconfig"
PV = "${LINUX_VERSION}+git${SRCPV}"
SRC_URI = "${REPO};branch=${BRANCH};protocol=https"
SRC_URI:append:sparrow-hawk = " \
    file://sparrow_hawk.cfg \
    file://sparrow-hawk-enable-i2c3-i2c4.dtsi;subdir=git/arch/arm64/boot/dts/renesas/ \
    file://0002-HACK-drivers-gpu-drm-drm_file-Ingnore-flag-checking.patch \
"
# Add support Waveshare touchpanel
SRC_URI:append:sparrow-hawk = " \
    file://waveshare_touch.cfg \
    file://0001-drm-panel-Add-panel-driver-for-Waveshare-DSI-touchsc.patch \
    file://0002-drm-panel-Added-waveshare-13.3inch-panel.patch \
    file://0003-drm-panel-Added-waveshare-7.0inch-h-dsi-screen-suppo.patch \
    file://0004-input-Add-support-for-no-irq-to-ili210x-driver.patch \
    file://0005-arm64-dts-renesas-r8a779g3-Add-waveshare-13.3-DSI-FH.patch \
"
# Add support Olimex DSI HDMI converter
SRC_URI:append:sparrow-hawk = " \
    file://0001-Add-Olimex-MIPI-HDMI-adapter-support.patch \
    file://lt8912b.cfg \
"
S = "${WORKDIR}/git"

KERNEL_DEVICETREE:append:sparrow-hawk = " \
    renesas/r8a779g3-sparrow-hawk-camera-j1-imx219.dtbo \
    renesas/r8a779g3-sparrow-hawk-camera-j2-imx219.dtbo \
    renesas/r8a779g3-sparrow-hawk-camera-j1-imx462.dtbo \
    renesas/r8a779g3-sparrow-hawk-camera-j2-imx462.dtbo \
    renesas/r8a779g3-sparrow-hawk-camera-j1-imx708.dtbo \
    renesas/r8a779g3-sparrow-hawk-camera-j2-imx708.dtbo \
    renesas/r8a779g3-sparrow-hawk-fan-pwm.dtbo \
    renesas/r8a779g3-sparrow-hawk-fan-argon40.dtbo \
    renesas/r8a779g3-sparrow-hawk-rpi-display-2-5in.dtbo \
    renesas/r8a779g3-sparrow-hawk-rpi-display-2-7in.dtbo \
"
KERNEL_DEVICETREE:append:sparrow-hawk = " \
    renesas/r8a779g3-sparrow-hawk-dsi-waveshare-panel.dtbo \
"
KERNEL_DEVICETREE:append:sparrow-hawk = " \
    renesas/r8a779g3-sparrow-hawk-dsi-mipi-hmdi.dtbo \
"

BBCLASSEXTEND ?= ""

do_compile:prepend:sparrow-hawk () {
    echo '#include "sparrow-hawk-enable-i2c3-i2c4.dtsi"' >>  ${S}/arch/arm64/boot/dts/renesas/r8a779g3-sparrow-hawk.dts
}

do_compile_kernelmodules:append () {
    if (grep -q -i -e '^CONFIG_MODULES=y$' ${B}/.config); then
        # 5.10+ kernels have module.lds that we need to copy for external module builds
        if [ -e "${B}/scripts/module.lds" ]; then
            install -Dm 0644 ${B}/scripts/module.lds ${STAGING_KERNEL_BUILDDIR}/scripts/module.lds
        fi
    fi
}

do_deploy:append() {
    # Remove the redundant device tree file (<device_tree>-<MACHINE>.dtb) that was created in the deploy directory
    for dtbf in ${KERNEL_DEVICETREE}; do
        dtb=`normalize_dtb "$dtbf"`
        dtb_ext=${dtb##*.}
        dtb_base_name=`basename $dtb .$dtb_ext`
        rm -f $deployDir/$dtb_base_name-${KERNEL_DTB_LINK_NAME}.$dtb_ext
    done
}
