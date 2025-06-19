DESCRIPTION = "Expand rootfs script"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"
RDEPENDS:${PN} = "bash e2fsprogs e2fsprogs-resize2fs parted"

SCRIPT_NAME = "${PN}.sh"
SRC_URI = " \
    file://${SCRIPT_NAME} \
"

S = "${WORKDIR}"
do_compile[noexec] = "1"
do_install () {
    install -d ${D}/${USRBINPATH}
    install -m 0755 ${WORKDIR}/${SCRIPT_NAME} ${D}/${USRBINPATH}
}

