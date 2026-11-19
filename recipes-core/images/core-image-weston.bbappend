# gtk+3-demo is not used on the target. Drop it to avoid building gtk+3,
# which pulls in librsvg-native and the Rust toolchain.
CORE_IMAGE_BASE_INSTALL:remove:sparrow-hawk = "gtk+3-demo"
