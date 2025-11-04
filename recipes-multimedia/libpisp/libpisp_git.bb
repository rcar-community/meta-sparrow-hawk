DESCRIPTION = "A helper library to generate run-time configuration for the Raspberry Pi ISP (PiSP)"
SECTION = "multimedia"
HOMEPAGE = "https://github.com/raspberrypi/libpisp"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS:append = " nlohmann-json"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=cb641bc04cda31daea161b1bc15da69f"

BRANCH = "main"
SRC_URI = "git://github.com/raspberrypi/libpisp.git;branch=${BRANCH};protocol=https"
# v1.2.1
SRCREV = "981977ff21f32c8a97d2a0ecbdff3e39d42ccce3"
UPSTREAM_CHECK_COMMITS = "1"

EXTRA_OEMESON += "-Dlogging=disabled"
S = "${WORKDIR}/git"

inherit meson pkgconfig

