require recipes-kernel/linux-libc-headers/linux-libc-headers.inc

# LINUX_VERSION/REPO/BRANCH/SRCREV are defined in inc file
require recipes-kernel/linux/kernel_6.12.inc

COMPATIBLE_MACHINE = "(rcar-gen4)"

SRC_URI = "${REPO};branch=${BRANCH};protocol=https"
KERNEL_DEFCONFIG = "renesas_defconfig"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

S = "${WORKDIR}/git"
