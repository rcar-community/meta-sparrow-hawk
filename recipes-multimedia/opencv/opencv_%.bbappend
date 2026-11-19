# Use the OpenCV native Wayland highgui backend instead of GTK to show
# windows on Wayland. GTK pulls in librsvg-native (via adwaita-icon-theme),
# which needs the Rust toolchain and increases the build time.
PACKAGECONFIG[wayland] = "-DWITH_WAYLAND=ON,-DWITH_WAYLAND=OFF,wayland-native wayland wayland-protocols libxkbcommon,"
PACKAGECONFIG:append:sparrow-hawk = " wayland"
PACKAGECONFIG:append:sparrow-hawk = " opencl"
PACKAGECONFIG:append:sparrow-hawk = " dnn"

# Not used on the target and increase the build time
PACKAGECONFIG:remove:sparrow-hawk = "samples gapi gphoto2"
