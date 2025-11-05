DESCRIPTION = "Custom FIT image with BL31, DTB, and Kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "sparrow-hawk"

inherit deploy

DEPENDS += " \
    u-boot-mkimage-native dtc-native \
    virtual/kernel arm-trusted-firmware \
"

SRC_URI = " \
    file://fit-image.its \
"

FILES:${PN} += " \
    /boot/fitImage \
"
ALLOW_EMPTY:${PN} = "1"

do_configure[noexec] = "1"
do_compile[depends] += "virtual/kernel:do_deploy"
do_compile[depends] += "arm-trusted-firmware:do_deploy"

do_compile() {
    cd ${DEPLOY_DIR}/images/${MACHINE}
    install -m 644 ${WORKDIR}/fit-image.its ./
    sed -i "s/bl31.bin/bl31-${MACHINE}.bin/" ./fit-image.its
    mkimage -f ./fit-image.its ./fitImage

}
do_install() {
    cd ${DEPLOY_DIR}/images/${MACHINE}
    install -d ${D}/boot
    install -m 644 ./fitImage ${D}/boot
}

python __anonymous () {
    if d.getVar("PREFERRED_PROVIDER_virtual/kernel") != "linux-renesas":
        d.setVarFlag("do_compile", "noexec", "1")
        d.setVarFlag("do_install", "noexec", "1")
}
