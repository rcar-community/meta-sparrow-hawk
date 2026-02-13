FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

require u-boot-common.inc
require u-boot.inc

COMPATIBLE_MACHINE = "(sparrow-hawk)"

DEPENDS += "lzop-native srecord-native"
DEPENDS += "bc-native dtc-native python3-pyelftools-native gnutls-native"

UBOOT_URL = "git://source.denx.de/u-boot/u-boot.git"
BRANCH = "master"
SRCREV = "127a42c7257a6ffbbd1575ed1cbaa8f5408a44b3"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

SRC_URI = "${UBOOT_URL};branch=${BRANCH};protocol=https"

PV = "v2026.01+git${SRCPV}"

UBOOT_SREC_SUFFIX = "srec"
UBOOT_SREC ?= "u-boot-elf.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_IMAGE ?= "u-boot-elf-${MACHINE}-${PV}-${PR}.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_SYMLINK ?= "u-boot-elf-${MACHINE}.${UBOOT_SREC_SUFFIX}"

# Backport to enable PCIe feature
SRC_URI:append = " file://0001-arm64-dts-renesas-r8a779g3-Reinstate-basic-PCIe-cloc.patch"

# Backport to support over 2GB files and RAM bank, and enable wget command
SRC_URI:append = "\
    file://0001-gunzip-Fix-len-parameter-in-function-signature.patch \
    file://0002-net-Stop-conflating-return-value-with-file-size-in-n.patch \
    file://0003-net-tftp-Fix-TFTP-Transfer-Size-data-type.patch \
    file://0004-arm-renesas-Enable-wget-command-and-TCP-on-all-R-Car.patch \
    file://0005-lmb-Reinstate-access-to-memory-above-ram_top.patch \
"

SRC_URI:append = "\
    file://nfs_cmd.cfg \
    file://mmc_boot.cfg \
    file://preboot.cfg \
    file://env_source_file.cfg \
    file://sparrowhawk.env;subdir=git/board/renesas/sparrowhawk/ \
"

do_deploy:append() {
    if [ -n "${UBOOT_CONFIG}" ]
    then
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    type=${type#*_}
                    install -m 644 ${B}/${config}/${UBOOT_SREC} ${DEPLOYDIR}/u-boot-elf-${type}-${PV}-${PR}.${UBOOT_SREC_SUFFIX}
                    cd ${DEPLOYDIR}
                    ln -sf u-boot-elf-${type}-${PV}-${PR}.${UBOOT_SREC_SUFFIX} u-boot-elf-${type}.${UBOOT_SREC_SUFFIX}
                fi
            done
            unset j
        done
        unset i
    else
        install -m 644 ${B}/${UBOOT_SREC} ${DEPLOYDIR}/${UBOOT_SREC_IMAGE}
        cd ${DEPLOYDIR}
        rm -f ${UBOOT_SREC} ${UBOOT_SREC_SYMLINK}
        ln -sf ${UBOOT_SREC_IMAGE} ${UBOOT_SREC_SYMLINK}
        ln -sf ${UBOOT_SREC_IMAGE} ${UBOOT_SREC}
        install -m 644 ${B}/flash.bin ${DEPLOYDIR}/
    fi
}

# Install flash.bin into rootfs
FILES:${PN} += "/flash.bin"
do_install:append () {
    install -d ${D}/boot
    install -m 0644 ${B}/flash.bin ${D}
}

