SUMMARY = "Recipe for libegl"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "(rcar-gen4)"

DEPENDS = "gles-user-module \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-wsegl libgbm wayland-kms', '', d)} \
"

PR = "r0"

RDEPENDS:${PN} = " \
    gles-user-module \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-wsegl libgbm wayland-kms', '', d)} \
"

PROVIDES = "virtual/egl"
RPROVIDES:${PN} += " \
    libegl \
    libegl1 \
"
