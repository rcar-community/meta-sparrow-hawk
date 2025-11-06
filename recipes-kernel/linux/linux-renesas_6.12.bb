DESCRIPTION = "Linux kernel for the R-Car board"

require recipes-kernel/linux/linux-yocto.inc

# LINUX_VERSION/REPO/BRANCH/SRCREV are defined in inc file
require recipes-kernel/linux/kernel_6.12.inc

COMPATIBLE_MACHINE = "(rcar-gen3|rcar-gen4)"

#KERNEL_VERSION_SANITY_SKIP = "1"
PV = "${LINUX_VERSION}+git${SRCPV}"

SRC_URI = "${REPO};branch=${BRANCH};protocol=https"
KERNEL_DEFCONFIG = "renesas_defconfig"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

S = "${WORKDIR}/git"

# For generating defconfig
KCONFIG_MODE = "--alldefconfig"
KBUILD_DEFCONFIG = "defconfig"

FILESEXTRAPATHS:prepend:sparrow-hawk = "${TOPDIR}/../../firmware:"
SRC_URI:append:sparrow-hawk = " \
    file://sparrow_hawk.cfg \
    file://sparrow-hawk-enable-i2c3-i2c4.dtsi;subdir=git/arch/arm64/boot/dts/renesas/ \
    file://0002-HACK-drivers-gpu-drm-drm_file-Ingnore-flag-checking.patch \
"
KBUILD_DEFCONFIG:sparrow-hawk = "renesas_defconfig"
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

# Add support Waveshare touchpanel
SRC_URI:append:sparrow-hawk = " \
    file://waveshare_touch.cfg \
    file://0001-drm-panel-Add-panel-driver-for-Waveshare-DSI-touchsc.patch \
    file://0002-drm-panel-Added-waveshare-13.3inch-panel.patch \
    file://0003-drm-panel-Added-waveshare-7.0inch-h-dsi-screen-suppo.patch \
    file://0004-input-Add-support-for-no-irq-to-ili210x-driver.patch \
    file://0005-arm64-dts-renesas-r8a779g3-Add-waveshare-13.3-DSI-FH.patch \
"
KERNEL_DEVICETREE:append:sparrow-hawk = " \
    renesas/r8a779g3-sparrow-hawk-dsi-waveshare-panel.dtbo \
"

do_compile:prepend:sparrow-hawk () {
    echo '#include "sparrow-hawk-enable-i2c3-i2c4.dtsi"' >>  ${S}/arch/arm64/boot/dts/renesas/r8a779g3-sparrow-hawk.dts
}

do_src_package_preprocess () {
    # Trim build paths from comments in generated sources to ensure reproducibility
    sed -i -e "s,${S}/,,g" \
            -e "s,${B}/,,g" \
        ${B}/drivers/video/logo/logo_linux_clut224.c \
        ${B}/drivers/tty/vt/consolemap_deftbl.c \
        ${B}/lib/oid_registry_data.c
}
addtask do_src_package_preprocess after do_compile before do_install

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
