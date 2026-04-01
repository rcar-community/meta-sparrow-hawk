SUMMARY = "BSP information for own system"
DESCRIPTION = "It includes BSP version, distro and build environment."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

PACKAGE_ARCH = "${MACHINE_ARCH}"

BSP_VERSION ??= "v2026-04-04"
KERNEL_VERSION = "${@bb.utils.contains("PREFERRED_PROVIDER_virtual/kernel", "linux-dummy", \
    "Built by linux-dummy", \
    "${@oe.utils.read_file('${STAGING_KERNEL_BUILDDIR}/kernel-abiversion')}", d)}"

do_install() {
    install -d ${D}${sysconfdir}/

    # Write BSP version
    printf "BSP version: ${BSP_VERSION}\n\n" >> ${D}${sysconfdir}/bspinfo

    # Yocto version and codename
    printf "${DISTRO_NAME} " >> ${D}${sysconfdir}/bspinfo

    distro_version_nodate=${@'${DISTRO_VERSION}'.replace('snapshot-${DATE}','snapshot').replace('${DATE}','')}
    printf "%s " $distro_version_nodate >> ${D}${sysconfdir}/bspinfo

    printf "(${DISTRO_CODENAME})" >> ${D}${sysconfdir}/bspinfo
    echo >> ${D}${sysconfdir}/bspinfo

    # Linux kernel
    printf "Linux kernel: ${KERNEL_VERSION} " >> ${D}${sysconfdir}/bspinfo
    echo >> ${D}${sysconfdir}/bspinfo

    # Adding Yocto layer information
    echo "" >> ${D}${sysconfdir}/bspinfo

    # Other layers
    for layer in `ls -d ${TOPDIR}/../poky ${TOPDIR}/../meta-*`; do
        LAYER_NAME=$(basename $layer)
        BRANCH=$(git -C ${layer} branch | grep \* | awk '{print $2}')
        COMMIT=$(git -C ${layer} rev-parse HEAD)

        # Output results if layer is used
        if grep -q ${LAYER_NAME} ${TOPDIR}/conf/bblayers.conf; then
            echo "${LAYER_NAME}=\"${BRANCH}:${COMMIT}\"" >> ${D}${sysconfdir}/bspinfo
        fi
    done
}
