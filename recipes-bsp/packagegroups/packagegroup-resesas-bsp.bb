SUMMARY = "Package group for Renesas board"
LICENSE = "MIT"

COMPATIBLE_MACHINE = "(rcar-gen4)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PR = "r0"

PACKAGES = " \
    packagegroup-renesas-bsp-tools \
    packagegroup-renesas-bsp-demo \
"

RDEPENDS:packagegroup-renesas-bsp-tools = " \
    v4l-utils \
    i2c-tools \
    coreutils \
    alsa-utils \
    v4l-utils \
    yavta \
    libcamera \
    libcamera-pycamera \
    libcamera-gst \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    nvme-cli \
    can-utils \
    expand-rootfs \
    pciutils \
    usbutils \
    libgpiod \
"

RDEPENDS:packagegroup-renesas-bsp-demo = " \
    python3-pip \
    python3-gpiod \
"
