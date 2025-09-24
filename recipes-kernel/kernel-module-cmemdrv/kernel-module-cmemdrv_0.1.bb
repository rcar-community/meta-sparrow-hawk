SUMMARY = "Cache Memory Primitive Module"
DESCRIPTION = "cmem driver"
LICENSE = "GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = " \
    file://${S}/GPL-COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://${S}/MIT-COPYING;md5=fea016ce2bdf2ec10080f69e9381d378 \
"

require include/rcar-bsp-modules-common.inc

inherit module

COMPATIBLE_MACHINE = "(rcar-gen3|rcar-gen4)"
DEPENDS = "linux-renesas"

PN = "kernel-module-cmemdrv"
DEPENDS = "linux-renesas"
KERNEL_MODULE_PACKAGE_SUFFIX = ""

RENESAS_CMEM_URL ?= "git://github.com/renesas-rcar/cmem.git;protocol=https"
SRC_URI = "${RENESAS_CMEM_URL};nobranch=1"
SRCREV:rcar-v3x = "abe21c26b5ad06909017966c7208c454e7f02b92"
SRCREV:rcar-gen4 = "${@oe.utils.conditional("RGID_ON", "1", "27cffd2a3b3fb866e5bab877ce817804de047330", "e67f473cddb089f71abead8bf18f618d42f515da", d )}"

S = "${WORKDIR}/git"

SRC_URI:append:sparrow-hawk = " \
    file://fix_build_error_612.patch \
    file://0001-WIP-Fix-DMA-mask-not-set.patch \
    file://0002-Fix-allocation-of-cmem-_other-regions.patch \
    file://0001-Fix-bit_ranges-is-not-correct-value.patch \
    file://0001-WIP-Use-physical-address-directly.patch \
"

FILES:${PN}:append = " \
    ${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/cmem.ko \
    /usr/include \
"

PACKAGES = " \
    ${PN} \
    ${PN}-dev \
    ${PN}-dbg \
"

do_install:append () {
    install -d ${D}${includedir}/linux
    install -m 644 ${S}/cmemdrv.h ${D}${includedir}/linux/
}

KERNEL_MODULE_AUTOLOAD += "cmemdrv"
KERNEL_MODULE_PROBECONF += "cmemdrv"
module_conf_cmemdrv = "${@oe.utils.conditional("RGID_ON", "1", "options cmemdrv bsize=0x1f000000", "options cmemdrv bsize=0x20000000", d )}"

