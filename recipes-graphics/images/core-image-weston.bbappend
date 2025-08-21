require recipes-core/images/core-image-minimal.inc

IMAGE_INSTALL:append:rcar-gen4 = " u-boot"
IMAGE_INSTALL:append:rcar-gen4 = " linux-fitimage"
IMAGE_INSTALL:append:rcar-gen4 = " kernel-modules"
IMAGE_INSTALL:append:rcar-gen4 = " sparrow-hawk-fw"
IMAGE_INSTALL:append:rcar-gen4 = " pciutils usbutils"
IMAGE_INSTALL:append:rcar-gen4 = " libgpiod python3-pip python3-gpiod"

# Wayland/Weston packages
IMAGE_INSTALL:append:rcar-gen4 = " \
    packagegroup-renesas-graphics \
"

EXTRA_IMAGEDEPENDS:rcar-gen4 += " ipl-burning"
