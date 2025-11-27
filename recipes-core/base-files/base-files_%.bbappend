FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}/:"

BSP_VERSION ??= "v2025-11-28"

SRC_URI:append = " \
    file://motd \
"

do_install:append () {
    printf "BSP version: ${BSP_VERSION}\n\n" >> ${D}${sysconfdir}/motd
}

inherit module-base

do_install_basefilesissue:append () {
    # Yocto version and codename
    printf "${DISTRO_NAME} " >> ${D}${sysconfdir}/issue.e2

    distro_version_nodate=${@'${DISTRO_VERSION}'.replace('snapshot-${DATE}','snapshot').replace('${DATE}','')}
    printf "%s " $distro_version_nodate >> ${D}${sysconfdir}/issue.e2

    printf "(${DISTRO_CODENAME})" >> ${D}${sysconfdir}/issue.e2
    echo >> ${D}${sysconfdir}/issue.e2

    # Linux kernel
    printf "Linux kernel: ${KERNEL_VERSION} " >> ${D}${sysconfdir}/issue.e2
    echo >> ${D}${sysconfdir}/issue.e2
}

