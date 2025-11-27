# Add overrides for selection 32 bit or 64 bit target
OVERRIDES .= ":${TARGET_SYS}"

# Basic packages
IMAGE_INSTALL:append = " \
    ${CORE_IMAGE_BASE_INSTALL} \
    bash \
    packagegroup-renesas-bsp-tools \
    packagegroup-renesas-bsp-demo \
"
# Sparrow-hawk specific packages
IMAGE_INSTALL:append:sparrow-hawk = " \
    example-apps \
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

