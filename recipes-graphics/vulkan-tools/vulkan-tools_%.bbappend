FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-cube-Allow-overriding-wayland-scanner-and-protocols-.patch"

# Build vkcube for Wayland
DEPENDS += "wayland-native wayland-protocols"
EXTRA_OECMAKE:remove = "-DBUILD_CUBE=OFF"
EXTRA_OECMAKE += " \
    -DBUILD_CUBE=ON \
    -DCUBE_WSI_SELECTION=WAYLAND \
    -DWAYLAND_SCANNER_EXECUTABLE=${STAGING_BINDIR_NATIVE}/wayland-scanner \
    -DWAYLAND_PROTOCOLS_PATH=${STAGING_DATADIR}/wayland-protocols \
"
