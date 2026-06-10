SUMMARY = "Binary firmware for Sparrow hawk board"
LICENSE = "CLOSED"
PR = "r0"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "sparrow-hawk"

PCIE_FIRMWARE = "https://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git/plain/rcar_gen4_pcie.bin?h=20260519;md5sum=293bdf19d8e16d3c4d8179e438db921b"
PCIE_FIRMWARE_LIC = "https://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git/plain/LICENCE.r8a779g_pcie_phy?h=20260519;md5sum=0b20e76a9a004b83c4a1c87e2153bbad"

SRC_URI:append:sparrow-hawk = " \
    ${PCIE_FIRMWARE} \
    ${PCIE_FIRMWARE_LIC} \
"

do_compile[noexec] = "1"
FILES:${PN}:sparrow-hawk += "/usr/lib/firmware/"

do_install:append:sparrow-hawk () {
    install -d ${D}/usr/lib/firmware
    install -m 755 ${WORKDIR}/rcar_gen4_pcie.bin* ${D}/usr/lib/firmware/rcar_gen4_pcie.bin
    install -m 755 ${WORKDIR}/LICENCE.r8a779g_pcie_phy* ${D}/usr/lib/firmware/LICENCE.r8a779g_pcie_phy
}

