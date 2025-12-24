SUMMARY = "Binary firmware for Sparrow hawk board"
SECTION = "kernel"
LICENSE = "Firmware-r8a779g_pcie_phy"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "sparrow-hawk"

LIC_FILES_CHKSUM = "file://LICENCE.r8a779g_pcie_phy;md5=0b20e76a9a004b83c4a1c87e2153bbad"
NO_GENERIC_LICENSE[Firmware-r8a779g_pcie_phy] = "LICENCE.r8a779g_pcie_phy"

S = "${WORKDIR}/git"
SRC_URI = " \
    git://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git;protocol=https;branch=main \
"
SRCREV = "56bb432a65bce10ff415231c3cdbf50cc81c03a6"

do_compile[noexec] = "1"

PACKAGES = "${PN}-license ${PN}"
LICENSE:${PN} = "Firmware-r8a779g_pcie_phy"
FILES:${PN}-license = "/usr/lib/firmware/LICENCE.r8a779g_pcie_phy"
FILES:${PN} = "/usr/lib/firmware/*"
RDEPENDS:${PN} += "${PN}-license"

do_install:append:sparrow-hawk () {
    install -d ${D}/usr/lib/firmware
    install -m 644 ${S}/rcar_gen4_pcie.bin ${D}/usr/lib/firmware
    install -m 644 ${S}/LICENCE.r8a779g_pcie_phy ${D}/usr/lib/firmware
}
