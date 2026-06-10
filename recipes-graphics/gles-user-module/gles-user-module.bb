DESCRIPTION = "PowerVR GPU user module"
LICENSE = "CLOSED"

require include/rcar-gfx-common.inc

COMPATIBLE_MACHINE = "(rcar-gen4)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS:${PN} = " \
    kernel-module-gles \
"

PN = "gles-user-module"
PR = "r0"

SRC_URI:r8a779g3 = "${GFX_LIBRARY_URL};sha256sum=${GFX_LIBRARY_SHA256}"

SRC_URI:append:rcar-gen4 = " \
    file://rc.pvr.service \
"

S = "${WORKDIR}/rogue"

# Support Systemd
inherit systemd
SYSTEMD_SERVICE:${PN} = "rc.pvr.service"

do_populate_lic[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Install configuration files
    install -d ${D}${sysconfdir}
    install -m 644 ${S}/etc/powervr.ini ${D}${sysconfdir}/powervr.ini
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 755 ${S}/etc/udev/rules.d/72-pvr-seat.rules ${D}${sysconfdir}/udev/rules.d/72-pvr-seat.rules

    # Install pre-built binaries
    install -d ${D}${libdir}
    install -m 755 ${S}/usr/lib/*.so ${D}${libdir}/
    install -d ${D}${libdir}/firmware
    install -m 644 ${S}/usr/lib/firmware/* ${D}${libdir}/firmware/

    # Install ICD (Installable Client Driver) Manifest File for vulkan-loader
    install -d ${D}${datadir}/vulkan/icd.d
    install -m 644 ${S}/usr/share/vulkan/icd.d/powervr_icd.json ${D}${datadir}/vulkan/icd.d/

    # Install ICD (Installable Client Driver) File for opencl-icd-loader
    install -d ${D}${sysconfdir}/OpenCL/vendors
    install -m 644 ${S}/etc/OpenCL/vendors/IMG.icd ${D}${sysconfdir}/OpenCL/vendors/

    # Install to run with Systemd service
    install -d ${D}${systemd_system_unitdir}/
    install -m 644 ${WORKDIR}/rc.pvr.service ${D}${systemd_system_unitdir}/
    install -d ${D}${bindir}
    install -m 755 ${S}/etc/init.d/rc.pvr ${D}${bindir}/pvrinit
}

FILES:${PN} = " \
    ${sysconfdir}/* \
    ${libdir}/*.so* \
    ${libdir}/firmware/* \
    ${datadir}/vulkan/icd.d/* \
    ${sysconfdir}/OpenCL/vendors/* \
    ${bindir}/* \
"

FILES:${PN}-dev = " \
"

INSANE_SKIP:${PN} = "ldflags build-deps file-rdeps"
INSANE_SKIP:${PN}-dev = "ldflags build-deps file-rdeps"
INSANE_SKIP:${PN} += "arch"
INSANE_SKIP:${PN}-dev += "arch"
INSANE_SKIP:${PN}-dbg = "arch"

# To avoid QA Issue: already-stripped errors and not stripped libs from packages
INSANE_SKIP:${PN} += "already-stripped"
