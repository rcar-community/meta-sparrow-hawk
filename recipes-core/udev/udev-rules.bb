SUMMARY = "udev rules for R-Car CV Library"
LICENSE = "MIT"
# nooelint: oelint.var.licenseremotefile - This recipe uses common license file
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://51-uio.rules \
    file://52-cmem.rules \
"

do_install () {
    install -d ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/51-uio.rules ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/52-cmem.rules ${D}${sysconfdir}/udev/rules.d/
}
