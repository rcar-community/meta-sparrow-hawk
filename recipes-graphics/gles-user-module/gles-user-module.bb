DESCRIPTION = "PowerVR GPU user module"
LICENSE = "CLOSED"

require include/rcar-gfx-common.inc

COMPATIBLE_MACHINE = "(rcar-gen4)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PN = "gles-user-module"
PR = "r0"

SRC_URI:r8a779g3 = "${GFX_LIBRARY_URL};sha256sum=${GFX_LIBRARY_SHA256}"

SRC_URI:append:rcar-gen4 = " \
    file://rc.pvr.service \
"

S = "${UNPACKDIR}/rogue"

inherit update-rc.d systemd

INITSCRIPT_NAME = "pvrinit"
INITSCRIPT_PARAMS = "start 7 5 2 . stop 62 0 1 6 ."
SYSTEMD_SERVICE:${PN} = "rc.pvr.service"

do_populate_lic[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Install configuration files
    install -d ${D}${sysconfdir}/init.d
    cp -r ${S}/etc/* ${D}${sysconfdir}/
    install -m 755 ${S}/etc/init.d/rc.pvr ${D}${sysconfdir}/init.d/pvrinit

    # Install header files except OpenCL standard header
    install -d ${D}${includedir}/
    cp -r ${S}/usr/include/* ${D}${includedir}/
    rm -rf ${D}${includedir}/CL

    # Install pre-builded binaries
    install -d ${D}${libdir}
    install -m 755 ${S}/usr/lib/*.so ${D}${libdir}/
    install -d ${D}/usr/local/bin
    install -m 755 ${S}/usr/local/bin/* ${D}/usr/local//bin/
    install -d ${D}${nonarch_base_libdir}/firmware
    install -m 644 ${S}/lib/firmware/* ${D}${nonarch_base_libdir}/firmware/

    # Remove conflict binary
    rm -rf ${D}${libdir}/libOpenCL.so

    # Install pkgconfig
    install -d ${D}${libdir}/pkgconfig
    install -m 644 ${S}/usr/lib/pkgconfig/*.pc ${D}${libdir}/pkgconfig/

    # Create symbolic link
    cd ${D}${libdir}
    ln -s libEGL.so libEGL.so.1
    ln -s libGLESv2.so libGLESv2.so.2

    # Install systemd service
    install -d ${D}${systemd_system_unitdir}/
    install -m 644 ${UNPACKDIR}/rc.pvr.service ${D}${systemd_system_unitdir}/
    install -d ${D}${exec_prefix}/bin
    install -m 755 ${S}/etc/init.d/rc.pvr ${D}${exec_prefix}/bin/pvrinit
}

PACKAGES = "\
    ${PN} \
    libegl-${PN} \
    libgles2-${PN} \
    ${PN}-dev \
    libegl-${PN}-dev \
    libgles2-${PN}-dev \
"

FILES:${PN} = " \
    ${sysconfdir}/* \
    ${libdir}/*.so* \
    ${nonarch_base_libdir}/firmware/rgx.fw* \
    ${nonarch_base_libdir}/firmware/rgx.sh* \
    /usr/local/bin/* \
    ${exec_prefix}/bin/* \
"

FILES:${PN}-dev = " \
    ${includedir}/* \
    ${libdir}/pkgconfig/* \
"

FILES:libegl-${PN} = "${libdir}/libEGL.so*"
FILES:libgles2-${PN} = "${libdir}/libGLESv2.so*"

FILES:libegl-${PN}-dev = " \
    ${libdir}/libEGL.* \
    ${includedir}/EGL \
    ${includedir}/KHR/khrplatform.h \
    ${libdir}/pkgconfig/egl.pc \
"
FILES:libgles2-${PN}-dev = " \
    ${libdir}/libGLESv2.* \
    ${includedir}/GLES2 \
    ${libdir}/pkgconfig/glesv2.pc \
"
FILES:libgles3-${PN}-dev = " \
    ${includedir}/GLES3 \
"

PROVIDES = "virtual/gles-user-module virtual/egl virtual/libgles2"

RPROVIDES:libegl-${PN} = "libegl"
RPROVIDES:libegl-${PN}-dev = "libegl-dev"
RPROVIDES:libgles2-${PN} = "libgles2"
RPROVIDES:libgles2-${PN}-dev = "libgles2-dev"
RPROVIDES:libgles3-${PN}-dev = "libgles3-dev"

RDEPENDS:${PN} = " \
    kernel-module-gles \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'libgbm wayland-kms', '', d)} \
"

INSANE_SKIP:${PN} = "ldflags build-deps file-rdeps"
INSANE_SKIP:${PN}-dev = "ldflags build-deps file-rdeps"
INSANE_SKIP:${PN} += "arch"
INSANE_SKIP:${PN}-dev += "arch"
INSANE_SKIP:${PN}-dbg = "arch"

# To avoid QA Issue: already-stripped errors and not stripped libs from packages
# To avoid QA Issue: Files/directories were installed but not shipped in any package
INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "installed-vs-shipped"

# Skip debug split and strip of do_package()
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
