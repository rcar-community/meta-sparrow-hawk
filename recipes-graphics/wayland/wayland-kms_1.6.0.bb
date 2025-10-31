SUMMARY = "KMS library for Wayland"
LICENSE = "MIT"

COMPATIBLE_MACHINE = "(rcar-gen4)"

DEPENDS = "libdrm gles-user-module wayland wayland-native"

PV:append = "+git${SRCREV}"

SRC_URI = "git://github.com/renesas-rcar/wayland-kms.git;branch=rcar-gen3;protocol=https"

SRCREV = "15184e5bd3701938a6b30b8f03b471477fc742e8"

LIC_FILES_CHKSUM = "file://wayland-kms.c;beginline=6;endline=24;md5=5cdaac262c876e98e47771f11c7036b5"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

FILES:${PN} = "${libdir}/libwayland-kms.so.*"
FILES:${PN}-dev = " \
    ${libdir}/libwayland-kms.la \
    ${libdir}/libwayland-kms.so \
    ${libdir}/pkgconfig/* \
    ${includedir}/* \
"
FILES:${PN}-staticdev += "${libdir}/libwayland-kms.a"
