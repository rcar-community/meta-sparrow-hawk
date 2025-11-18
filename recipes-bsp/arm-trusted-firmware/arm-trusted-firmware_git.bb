DESCRIPTION = "ARM Trusted Firmware"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.rst;md5=1dd070c98a281d18d9eefd938729b031"

COMPATIBLE_MACHINE = "sparrow-hawk"
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy

PV:rcar-gen4:sparrow-hawk = "v2.14.0+upstream+git${SRCPV}"
BRANCH:rcar-gen4:sparrow-hawk = "master"
SRC_URI = "git://github.com/ARM-software/arm-trusted-firmware.git;branch=${BRANCH};protocol=https"
SRCREV:rcar-gen4:sparrow-hawk = "1d5aa939bc8d3d892e2ed9945fa50e36a1a924cc"

S = "${WORKDIR}/git"

CLEAN_OPT:rcar-gen4 = "clean_srecord"
BUILD_OPT:rcar-gen4 = "bl31 rcar_srecord"
PLATFORM:rcar-gen4 = "rcar_gen4"

ATFW_OPT ?= ""
ATFW_CONF ?= ""

sparrow_hawk_r8a779g3[default]     = "LSI=V4H CTX_INCLUDE_AARCH32_REGS=0 MBEDTLS_COMMON_MK=1 PTP_NONSECURE_ACCESS=1 LOG_LEVEL=20 DEBUG=0 ENABLE_ASSERTIONS=0 E=0"

# requires CROSS_COMPILE set by hand as there is no configure script
export CROSS_COMPILE="${TARGET_PREFIX}"

# Let the Makefile handle setting up the CFLAGS and LDFLAGS as it is a standalone application
CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

# do_install() nothing
do_install[noexec] = "1"

do_ipl_compile () {
    oe_runmake distclean
    oe_runmake ${CLEAN_OPT} PLAT=${PLATFORM} SPD=none MBEDTLS_COMMON_MK=1 ${ATFW_OPT}
    oe_runmake ${BUILD_OPT} PLAT=${PLATFORM} SPD=none MBEDTLS_COMMON_MK=1 ${ATFW_OPT}

    # Create ${S}/release folder to store output for compile tasks
    install -d ${S}/release

    # Move to ${S}/release and rename
    install ${S}/build/${PLATFORM}/release/bl31/bl31.elf                ${S}/release/bl31-${MACHINE}${ATFW_CONF}.elf
    install ${S}/build/${PLATFORM}/release/bl31.bin                     ${S}/release/bl31-${MACHINE}${ATFW_CONF}.bin
    install ${S}/build/${PLATFORM}/release/bl31.srec                    ${S}/release/bl31-${MACHINE}${ATFW_CONF}.srec
}

python do_compile () {
    soc = d.getVar('SOC_FAMILY')
    soc = soc.split(':')[1]
    print(soc)
    machine = d.getVar('MACHINE_ARCH')
    print(machine)
    confs_dict = d.getVarFlags(machine + "_" + soc)
    print(confs_dict)
    confs_list = list(confs_dict.keys())

    for conf in confs_list:
        d.setVar('ATFW_OPT', confs_dict[conf])
        if conf == "default":
            d.setVar('ATFW_CONF', "")
        else:
            d.setVar('ATFW_CONF', "-" + conf)
        bb.build.exec_func('do_ipl_compile', d)
}

do_deploy () {
    # Copy binary files to deploy directory
    install -m 0644 ${S}/release/*.elf  ${DEPLOYDIR}
    install -m 0644 ${S}/release/*.bin  ${DEPLOYDIR}
    install -m 0644 ${S}/release/*.srec ${DEPLOYDIR}
}

addtask deploy after do_compile
