FILESEXTRAPATHS:prepend:rcar-gen4 := "${THISDIR}/${PN}:"

SRC_URI:append:rcar-gen4 = " \
    file://0001-Add-sync_fence_info-and-sync_pt_info.patch \
    file://Add-libkms.patch \
"

PACKAGES:prepend:rcar-gen4 = "${PN}-kms "

PACKAGECONFIG:append:rcar-gen4 = " libkms"
PACKAGECONFIG:rcar-gen4[libkms] = "-Dlibkms=enabled,-Dlibkms=disabled"

FILES:${PN}-kms:rcar-gen4 = "${libdir}/libkms*.so.*"
