SUMMARY = "RWDT watchdog configuration for R-Car V4H"
DESCRIPTION = "Provide rwdt-watchdog.service that allows the RWDT reset in RST and enables the systemd runtime watchdog. The service is disabled by default. Run 'systemctl enable --now rwdt-watchdog.service' to enable it."
HOMEPAGE = "https://github.com/rcar-community/meta-sparrow-hawk"
BUGTRACKER = "https://github.com/orgs/rcar-community/discussions/categories/q-a"
SECTION = "base"
LICENSE = "MIT"
# nooelint: oelint.var.licenseremotefile - This recipe uses common license file
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
CVE_PRODUCT = ""

COMPATIBLE_MACHINE = "(sparrow-hawk)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = " \
    file://rwdt-watchdog.sh \
    file://rwdt-watchdog.service \
"
S = "${WORKDIR}"

inherit systemd

SYSTEMD_SERVICE:${PN} = "rwdt-watchdog.service"
# The watchdog resets the SoC on a heavy load or a hang, so users enable it.
SYSTEMD_AUTO_ENABLE = "disable"

# rwdt-watchdog.sh uses "busybox devmem", busctl and systemctl
RDEPENDS:${PN} = "busybox systemd"

do_compile[noexec] = "1"
do_install () {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/rwdt-watchdog.sh ${D}${sbindir}/rwdt-watchdog

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/rwdt-watchdog.service ${D}${systemd_system_unitdir}/
    sed -i -e 's|@SBINDIR@|${sbindir}|g' ${D}${systemd_system_unitdir}/rwdt-watchdog.service
}
