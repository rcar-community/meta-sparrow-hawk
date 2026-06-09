SUMMARY = "Renesas package group for Wayland/Weston and OpenGL ES"
LICENSE = "CLOSED & MIT"

COMPATIBLE_MACHINE = "(rcar-gen4)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PR = "r0"

PACKAGES = " \
    packagegroup-renesas-graphics \
    packagegroup-graphics-renesas-gles \
    packagegroup-graphics-oss-wayland \
    packagegroup-graphics-oss-opencl \
"

RDEPENDS:packagegroup-renesas-graphics = " \
    packagegroup-graphics-renesas-gles \
    packagegroup-graphics-oss-wayland \
    packagegroup-graphics-oss-opencl \
"

# GFX package
RDEPENDS:packagegroup-graphics-renesas-gles = " \
    kernel-module-gles \
    gles-user-module \
"

RDEPENDS:packagegroup-graphics-oss-wayland = " \
    wayland \
    weston \
    weston-examples \
    alsa-utils \
    alsa-tools \
    libdrm-tests \
"

RDEPENDS:packagegroup-graphics-oss-opencl = " \
    clinfo \
"
