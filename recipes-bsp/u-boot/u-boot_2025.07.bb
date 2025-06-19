FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

require u-boot-common.inc
require u-boot.inc

COMPATIBLE_MACHINE = "(sparrow-hawk)"

DEPENDS += "lzop-native srecord-native"
DEPENDS += "bc-native dtc-native python3-pyelftools-native gnutls-native"

UBOOT_URL = "git://source.denx.de/u-boot/custodians/u-boot-sh.git"
BRANCH = "master"
SRCREV = "feb55165233623648cb0a74953735b00ec6e322e"
SRC_URI[sha256sum] = "9e4706b93585e29ed2dd6cd964954a35f9baf8d218c461cf69a0b6c5d2ac9b4a"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

SRC_URI = "${UBOOT_URL};branch=${BRANCH};protocol=https"

SRC_URI:append = " \
    file://0001-arm64-renesas-Introduce-renesas_dram_init_banksize.patch \
    file://0002-arm64-dts-renesas-r8a779g3-Add-Retronix-R-Car-V4H-Sp.patch \
    file://0003-ARM-renesas-Enable-serial-RX-buffer-on-Renesas-R-Car.patch \
    file://0004-arm64-dts-renesas-sparrow-hawk-Inhibit-microSD-UHS-m.patch \
"

PV = "v2025.07+git${SRCPV}"

UBOOT_SREC_SUFFIX = "srec"
UBOOT_SREC ?= "u-boot-elf.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_IMAGE ?= "u-boot-elf-${MACHINE}-${PV}-${PR}.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_SYMLINK ?= "u-boot-elf-${MACHINE}.${UBOOT_SREC_SUFFIX}"

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

