SUMMARY = "wayland-wsegl library"
SECTION = "libs"
LICENSE = "MIT"

COMPATIBLE_MACHINE = "(rcar-gen4)"

DEPENDS = "libgbm wayland-kms libdrm wayland wayland-native wayland-protocols"

SRC_URI = "git://github.com/renesas-rcar/wayland-wsegl.git;branch=rcar_gen5;protocol=https"

SRCREV = "ef1e31db2b99bc3fe53d37e1dc8159d478505972"

LIC_FILES_CHKSUM = "file://src/waylandws.h;beginline=1;endline=22;md5=ebf7ec97b867b0329acbb2c4190fd7a9"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGES = " \
    ${PN} \
    ${PN}-dbg \
"

FILES:${PN} = " \
    ${libdir}/libpvrWAYLAND_WSEGL.so.* \
    ${libdir}/*.so \
"
FILES:${PN}-dbg += "${libdir}/libpvrWAYLAND_WSEGL/.debug/*"

INSANE_SKIP:${PN} += "dev-so"
