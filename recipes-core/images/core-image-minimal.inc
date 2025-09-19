# Add overrides for selection 32 bit or 64 bit target
OVERRIDES .= ":${TARGET_SYS}"

# Basic packages
IMAGE_INSTALL:append = " \
    bash \
    v4l-utils \
    i2c-tools \
    coreutils \
    kernel-devicetree \
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
    kernel-modules \
    u-boot \
    libgpiod \
    python3-pip \
    python3-gpiod \
"
# Sparrow-hawk specific packages
IMAGE_INSTALL:append:sparrow-hawk = " \
    linux-fitimage \
    sparrow-hawk-fw \
"

# Qos user packages only supports 64bit.
QOS_USER_PKGS = ""
QOS_USER_PKGS:aarch64-poky-linux = " \
    qosif-user-module \
    qosif-tp-user-module \
"

# Installation for QoS packages
IMAGE_INSTALL:append = " \
    kernel-module-qos \
    ${QOS_USER_PKGS} \
"

#Installation for ethtool
IMAGE_INSTALL:append = " ethtool"

# Enable package manager
EXTRA_IMAGE_FEATURES += "package-management"

# Enable ssh feature
EXTRA_IMAGE_FEATURES:append = " ssh-server-openssh"

