SUMMARY = "gbm library"
LICENSE = "MIT"
SECTION = "libs"

COMPATIBLE_MACHINE = "(rcar-gen4)"

DEPENDS = "wayland-kms udev"

SRC_URI = "git://github.com/renesas-rcar/libgbm;branch=match-mesa-20.0.1;protocol=https \
           file://Add-gbm_bo_get_fd_for_plane.patch \
           file://0001-Fix-installtion-path-for-header-libs.patch \
"

SRCREV = "538889dee7940cbcd8f384ff24436c785181cfdb"

LIC_FILES_CHKSUM = "file://gbm.c;beginline=4;endline=22;md5=5cdaac262c876e98e47771f11c7036b5"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGES = " \
    ${PN} \
    ${PN}-dev \
    ${PN}-dbg \
    ${PN}-staticdev \
"

FILES:${PN} = " \
    ${libdir}/libgbm.so.* \
    ${libdir}/libgbm_kms.so.* \
    ${libdir}/*.so \
"
FILES:${PN}-dev += "${libdir}/*.la"
FILES:${PN}-dbg += "${libdir}/.debug/*"
FILES:${PN}-staticdev += "${libdir}/*.a"

INSANE_SKIP:${PN} += "dev-so"

PROVIDES += "virtual/libgbm"
