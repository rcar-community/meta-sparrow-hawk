require recipes-core/images/core-image-minimal.inc

# Wayland/Weston packages
IMAGE_INSTALL:append:rcar-gen4 = " \
    packagegroup-renesas-graphics \
"
