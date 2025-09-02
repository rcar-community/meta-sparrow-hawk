FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV = "d54e5537ca0909339bb6950f3a565c9077406a3c"
# Remove patch from meta-openembedded/meta-multimedia
SRC_URI:remove = "file://0001-media_device-Add-bool-return-type-to-unlock.patch"

DEPENDS:append = " libpisp python3-pybind11 libdrm tiff libsdl2"

SRC_URI:append = " \
    file://0001-libcamera-ipa_manager-createIPA-Allow-matching-by-IP.patch \
    file://0002-ipa-rkisp1-Add-settings-for-DreamChip-RPPX1-ISP.patch \
    file://0003-libcamera-pipeline-Add-R-Car-Gen4-ISP-pipeline.patch \
    file://0004-ipa-rkisp1-Add-basic-CCM-calibration-for-imx219.patch \
"

PACKAGECONFIG:append = " pycamera"
LIBCAMERA_PIPELINES = "rcar-gen4,rkisp1"
EXTRA_OEMESON := " \
    --prefix=/usr/ \
    -Dpipelines=${LIBCAMERA_PIPELINES} \
    -Dipas=rkisp1 \
    -Dcam=enabled \
    -Dpycamera=enabled \
    -Dtest=false \
    -Ddocumentation=disabled \
"
