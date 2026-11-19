PACKAGECONFIG:append:sparrow-hawk = " kms wayland"

# Not used on the target. librsvg needs the Rust toolchain and increases the build time
PACKAGECONFIG:remove:sparrow-hawk = "rsvg"
