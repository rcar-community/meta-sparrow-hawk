SUMMARY = "Recipe for libegl"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "(rcar-gen4)"

DEPENDS = "user-module-gles \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-wsegl libgbm wayland-kms', '', d)} \
"

PR = "r0"

RDEPENDS:${PN} = " \
    user-module-gles \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland-wsegl libgbm wayland-kms', '', d)} \
"

PROVIDES = "virtual/egl"
RPROVIDES:${PN} += " \
    libegl \
    libegl1 \
"
