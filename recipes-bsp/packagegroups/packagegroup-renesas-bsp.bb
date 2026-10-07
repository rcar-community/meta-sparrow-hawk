SUMMARY = "Package group for Renesas board"
LICENSE = "MIT"

COMPATIBLE_MACHINE = "(rcar-gen4)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PR = "r0"

PACKAGES = " \
    packagegroup-renesas-bsp-tools \
    packagegroup-renesas-bsp-demo \
    packagegroup-renesas-bsp-ai-tools \
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
    lshw \
    util-linux \
"

RDEPENDS:packagegroup-renesas-bsp-demo = " \
    python3-pip \
    python3-gpiod \
    sqlite3 \
    python3-numpy \
    python3-sqlite3 \
    opencv \
"

# Development tools and libraries for the AI runtime
RDEPENDS:packagegroup-renesas-bsp-ai-tools = " \
    git gcc g++ make curl cmake openssl libffi libnsl2 \
    binutils patch zlib-dev libffi-dev openssl-dev bzip2 \
    readline sqlite3 ncurses tar opencv \
    libgomp \
    udev-rules bsp-config \
    packagegroup-opencv-sdk \
"
