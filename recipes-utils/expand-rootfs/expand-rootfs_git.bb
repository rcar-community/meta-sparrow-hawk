SUMMARY = "Expand rootfs helper script"
DESCRIPTION = "This recipes adds the helper script to expand root file partition to maximum size of disk."
HOMEPAGE = "https://github.com/rcar-community/meta-sparrow-hawk"
BUGTRACKER = "https://github.com/orgs/rcar-community/discussions/categories/q-a"
SECTION = "utils"
LICENSE = "MIT"
# nooelint: oelint.var.licenseremotefile - This recipe uses common license file
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
CVE_PRODUCT = ""

SCRIPT_NAME = "${PN}.sh"
SRC_URI = "file://${SCRIPT_NAME}"

S = "${UNPACKDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
RDEPENDS:${PN} = "bash e2fsprogs e2fsprogs-resize2fs parted"
BBCLASSEXTEND = ""

do_compile[noexec] = "1"
do_install () {
    install -d ${D}/${USRBINPATH}
    install -m 0755 ${UNPACKDIR}/${SCRIPT_NAME} ${D}/${USRBINPATH}
}

