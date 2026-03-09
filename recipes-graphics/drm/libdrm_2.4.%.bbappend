FILESEXTRAPATHS:prepend:rcar-gen4:class-target := "${THISDIR}/${PN}:"

SRC_URI:append:rcar-gen4:class-target = " \
    file://0001-Add-sync_fence_info-and-sync_pt_info.patch \
    file://Add-libkms.patch \
"

KMS_ENABLE = ""
KMS_ENABLE:rcar-gen4:class-target = "-Dlibkms=enabled"

KMS_DISABLE = ""
KMS_DISABLE:rcar-gen4:class-target = "-Dlibkms=disabled"

PACKAGECONFIG[libkms] = "${KMS_ENABLE},${KMS_DISABLE}"
PACKAGECONFIG:append:rcar-gen4:class-target = " libkms"

PACKAGES:prepend:rcar-gen4:class-target = "${PN}-kms "

FILES:${PN}-kms:rcar-gen4:class-target = "${libdir}/libkms*.so.*"
