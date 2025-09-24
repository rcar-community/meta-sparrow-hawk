# Add overrides for selection 32 bit or 64 bit target
OVERRIDES .= ":${TARGET_SYS}"

# Basic packages
IMAGE_INSTALL:append = " \
    ${CORE_IMAGE_BASE_INSTALL} \
    bash bsp-info \
    packagegroup-renesas-bsp-tools \
    packagegroup-renesas-bsp-demo \
"
# Sparrow-hawk specific packages
IMAGE_INSTALL:append:sparrow-hawk = " \
    example-apps \
    udev-rules \
    bsp-config \
"
# For AI runtime
IMAGE_INSTALL:append = "\
    git gcc g++ make curl cmake openssl libffi libnsl2 \
    binutils patch zlib-dev libffi-dev openssl-dev bzip2 \
    readline sqlite3 ncurses tar patch opencv \
    libgomp \
"
IMAGE_FEATURES += "dev-pkgs tools-sdk"

# Installation for cmem kernel module
IMAGE_INSTALL:append = " \
    kernel-module-cmemdrv \
"

# Qos user packages only supports 64bit.
QOS_USER_PKGS = ""
QOS_USER_PKGS:aarch64-poky-linux = " \
    qosif-user-module \
    qosif-tp-user-module \
"

OVERRIDES .= ":kernel-${PREFERRED_PROVIDER_virtual/kernel}"
# Installation for QoS packages
IMAGE_INSTALL:append:kernel-linux-renesas = " \
    ${QOS_USER_PKGS} \
    kernel-module-qos \
"

#Installation for ethtool
IMAGE_INSTALL:append = " ethtool"

# Enable package manager
EXTRA_IMAGE_FEATURES += "package-management"

# Enable ssh feature
EXTRA_IMAGE_FEATURES:append = " ssh-server-openssh"

# Fix SDK build error with apt package manger
TOOLCHAIN_TARGET_TASK:remove = "target-sdk-provides-dummy"

