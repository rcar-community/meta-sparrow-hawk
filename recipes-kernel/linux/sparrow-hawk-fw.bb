SUMMARY = "Backward Compatibility:Binary firmware for Sparrow hawk board"
LICENSE = "CLOSED"
PR = "r1"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "sparrow-hawk"

SRC_URI = ""

S = "${UNPACKDIR}"

PACKAGES = "${PN}"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN}:sparrow-hawk = " \
    linux-firmware-pcie-rcar \
    linux-firmware-r8a779g-pcie-phy-license \
"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
