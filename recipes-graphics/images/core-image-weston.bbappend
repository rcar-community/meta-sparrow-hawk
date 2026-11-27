require recipes-core/images/core-image-minimal.inc

IMAGE_INSTALL:append:rcar-gen4 = " glmark2"
# Wayland/Weston packages
IMAGE_INSTALL:append:rcar-gen4 = " \
    packagegroup-renesas-graphics \
"

# gtk+3-demo is not used on the target. Drop it to avoid building gtk+3,
# which pulls in librsvg-native and the Rust toolchain.
# core-image-weston-sdk.bbappend is a link to this file.
CORE_IMAGE_BASE_INSTALL:remove:sparrow-hawk = "gtk+3-demo"

# Test applications are not needed in the SDK (core-image-minimal-sdk too)
IMAGE_FEATURES:remove = "tools-testapps"
