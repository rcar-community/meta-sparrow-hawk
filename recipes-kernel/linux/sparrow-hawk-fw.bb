SUMMARY = "Binary firmware for Sparrow hawk board"
LICENSE = "CLOSED"
PR = "r0"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "sparrow-hawk"

FILESEXTRAPATHS:prepend:sparrow-hawk = "${THISDIR}/../../firmware:"

SRC_URI:append:sparrow-hawk = " \
    file://rcar_gen4_pcie.bin \
"

do_compile[noexec] = "1"
FILES:${PN}:sparrow-hawk += "/usr/lib/firmware/"

do_install:append:sparrow-hawk () {
    install -d ${D}/usr/lib/firmware
    install -m 755 ${WORKDIR}/rcar_gen4_pcie.bin ${D}/usr/lib/firmware
}

