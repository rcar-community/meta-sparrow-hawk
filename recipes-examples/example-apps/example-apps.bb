DESCRIPTION = "Example application for checking features of Sparrow-Hawk board"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "sparrow-hawk"

SRC_URI = " \
    file://toggle_gpio_GP2_12.py \
"

S = "${UNPACKDIR}"

FILES:${PN}:append = "/usr/bin/example-apps"

do_compile[noexec] = "1"
do_install () {
    install -d ${D}/${USRBINPATH}/example-apps/
    install -m 0755 ${UNPACKDIR}/toggle_gpio_GP2_12.py ${D}/${USRBINPATH}/example-apps
}

