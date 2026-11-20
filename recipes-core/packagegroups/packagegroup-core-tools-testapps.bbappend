# gst-examples needs gtk+3, which pulls in librsvg-native (via
# adwaita-icon-theme) and thus the Rust toolchain. It is not used on the
# target and increases the build time of core-image-minimal-sdk.
GSTEXAMPLES:sparrow-hawk = ""
