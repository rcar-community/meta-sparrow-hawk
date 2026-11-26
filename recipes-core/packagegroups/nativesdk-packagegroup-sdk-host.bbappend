RDEPENDS:${PN}:append = " \
    nativesdk-perl nativesdk-perl-dev \
    nativesdk-perl-modules \
    nativesdk-bmaptool \
"

# QEMU is not used with the SDK. Drop it to reduce the build time.
RDEPENDS:${PN}:remove = " \
    nativesdk-qemu \
    nativesdk-qemu-helper \
"
