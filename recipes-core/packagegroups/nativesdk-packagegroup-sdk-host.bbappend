RDEPENDS:${PN}:append = " \
    nativesdk-perl nativesdk-perl-dev \
    nativesdk-perl-modules \
    nativesdk-bmaptool \
"

# QEMU (runqemu and qemu-user) is not used with the SDK of the real board.
# Drop it to reduce the build time and the size of the SDK installer.
RDEPENDS:${PN}:remove = " \
    nativesdk-qemu \
    nativesdk-qemu-helper \
"
