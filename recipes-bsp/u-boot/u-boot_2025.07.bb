FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

require u-boot-common.inc
require u-boot.inc

COMPATIBLE_MACHINE = "(sparrow-hawk)"

DEPENDS += "lzop-native srecord-native"
DEPENDS += "bc-native dtc-native python3-pyelftools-native gnutls-native"

UBOOT_URL = "git://source.denx.de/u-boot/u-boot.git"
BRANCH = "master"
SRCREV = "e37de002fac3895e8d0b60ae2015e17bb33e2b5b"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

SRC_URI = "${UBOOT_URL};branch=${BRANCH};protocol=https"

PV = "v2025.07+git${SRCPV}"

UBOOT_SREC_SUFFIX = "srec"
UBOOT_SREC ?= "u-boot-elf.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_IMAGE ?= "u-boot-elf-${MACHINE}-${PV}-${PR}.${UBOOT_SREC_SUFFIX}"
UBOOT_SREC_SYMLINK ?= "u-boot-elf-${MACHINE}.${UBOOT_SREC_SUFFIX}"

# PCIe related patchset
SRC_URI:append = "\
    file://0001-drivers-pci-pcie_dw_common-Add-dw_pcie_link_set_max_.patch \
    file://0002-pci-pcie_dw_meson-Use-dw_pcie_link_set_max_link_widt.patch \
    file://0003-pci-pcie_dw_qcom-Use-dw_pcie_link_set_max_link_width.patch \
    file://0004-pci-pcie_dw_rockchip-Use-dw_pcie_link_set_max_link_w.patch \
    file://0005-pci-pcie-rcar-gen4-Add-Renesas-R-Car-Gen4-DW-PCIe-co.patch \
    file://0006-arm64-dts-renesas-r8a779g3-Enable-PCIe-NVMe-on-Retro.patch \
"

# EVT-B1 Fixes
SRC_URI:append = "\
    file://0007-mtd-spi-nor-ids-Add-support-for-Winbond-W77Q51NW.patch \
    file://0008-arm64-dts-renesas-r8a779g3-Describe-generic-SPI-NOR-.patch \
    file://0009-arm64-renesas-r8a779g3-Enable-Winbond-SPI-NOR-suppor.patch \
    file://0010-arm64-dts-renesas-r8a779g3-Invert-microSD-voltage-se.patch \
    file://0011-arm64-renesas-r8a779g3-Disable-MicroSD-UHS-modes-on-.patch \
    file://0012-arm64-renesas-r8a779g3-Disable-dual-rank-DRAM-on-Ret.patch \
    file://0013-arm64-renesas-r8a779g3-Enable-xHCI-USB-on-Retronix-R.patch \
    file://0014-FIXME-arm64-dts-renesas-r8a779g3-Set-VDDQ18_25_AVB-v.patch \
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

