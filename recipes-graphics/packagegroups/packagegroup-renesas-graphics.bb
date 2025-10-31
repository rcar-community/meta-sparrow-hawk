SUMMARY = "Renesas package group for Wayland/Weston and OpenGL ES"
LICENSE = "CLOSED & MIT"

COMPATIBLE_MACHINE = "(rcar-gen4)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PR = "r0"

PACKAGES = " \
    packagegroup-renesas-graphics \
    packagegroup-graphics-renesas-gles \
    packagegroup-graphics-renesas-wayland \
    packagegroup-graphics-oss-wayland \
"

RDEPENDS:packagegroup-renesas-graphics = " \
    packagegroup-graphics-renesas-gles \
    packagegroup-graphics-renesas-wayland \
    packagegroup-graphics-oss-wayland \
"

# GFX package
RDEPENDS:packagegroup-graphics-renesas-gles = " \
    kernel-module-gles \
    gles-user-module \
"

DEPENDS:packagegroup-graphics-renesas-wayland = "libegl libgles2"

RDEPENDS:packagegroup-graphics-renesas-wayland = " \
    libgbm \
    libgbm-dev \
    wayland-kms \
    wayland-wsegl \
    libdrm-kms \
"

RDEPENDS:packagegroup-graphics-oss-wayland = " \
    wayland \
    weston \
    weston-examples \
    alsa-utils \
    alsa-tools \
    libdrm-tests \
"
