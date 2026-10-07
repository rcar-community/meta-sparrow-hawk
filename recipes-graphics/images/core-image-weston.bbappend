require recipes-core/images/core-image-minimal.inc

IMAGE_INSTALL:append:rcar-gen4 = " glmark2"
# Wayland/Weston packages
IMAGE_INSTALL:append:rcar-gen4 = " \
    packagegroup-renesas-graphics \
"
