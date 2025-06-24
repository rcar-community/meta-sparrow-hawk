FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV = "633063e099ccb0618359f9ecde9b05852c34259d"
#SRCREV = "1537da74427791bb3b5880e7d002daf8ea42db31"
SRC_URI:remove = "file://0001-media_device-Add-bool-return-type-to-unlock.patch"

DEPENDS:append = " libpisp python3-pybind11 libdrm tiff libsdl2"

SRC_URI:append = " \
    file://0001-libcamera-ipa_manager-createIPA-Allow-matching-by-IP.patch \
    file://0002-ipa-rkisp1-Add-settings-for-DreamChip-RPPX1-ISP.patch \
    file://0003-libcamera-pipeline-Add-R-Car-Gen4-ISP-pipeline.patch \
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

