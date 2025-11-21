FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV = "b9fa6e0e61d3ea605fe4b1201ede5745cd5800e5"
# Remove patch from meta-openembedded/meta-multimedia
SRC_URI:remove = "file://0001-media_device-Add-bool-return-type-to-unlock.patch"

DEPENDS:append = " libpisp python3-pybind11 libdrm tiff libsdl2"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG:append = " gstreamer"

SRC_URI:append = " \
    file://0001-libcamera-ipa_manager-Create-IPA-by-name.patch \
    file://0002-ipa-ipa_module-Remove-pipelineName.patch \
    file://0003-ipa-meson.build-Remove-duplicated-variable.patch \
    file://0004-ipa-Allow-pipelines-to-have-differently-named-IPA.patch \
    file://0005-ipa-rkisp1-Add-settings-for-DreamChip-RPPX1-ISP.patch \
    file://0006-libcamera-pipeline-Add-R-Car-Gen4-ISP-pipeline.patch \
    file://0007-ipa-rkisp1-Add-basic-CCM-calibration-for-imx219.patch \
    file://0008-ipa-CameraSensorHelper-Add-CameraSensorHelperImx708.patch \
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
