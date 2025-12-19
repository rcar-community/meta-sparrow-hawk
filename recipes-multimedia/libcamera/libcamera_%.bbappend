FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PV = "v0.6.0+upstream+git${SRCPV}"
SRCREV = "f4c3dee21770b9b8817c80265b9f81eda1833731"
# Remove patch from meta-openembedded/meta-multimedia
SRC_URI:remove = "file://0001-media_device-Add-bool-return-type-to-unlock.patch"

DEPENDS:append = " libpisp python3-pybind11 libdrm tiff libsdl2"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG:append = " gstreamer"

SRC_URI:append = " \
    file://0001-libcamera-ipa_manager-Create-IPA-by-name.patch \
    file://0002-ipa-ipa_module-Remove-pipelineName.patch \
    file://0003-ipa-Allow-pipelines-to-have-differently-named-IPA.patch \
    file://0004-ipa-rkisp1-Add-settings-for-DreamChip-RPPX1-ISP.patch \
    file://0005-libcamera-pipeline-Add-R-Car-Gen4-ISP-pipeline.patch \
    file://0006-fixup-libcamera-pipeline-Add-R-Car-Gen4-ISP-pipeline.patch \
    file://0007-ipa-rkisp1-Add-basic-CCM-calibration-for-imx219.patch \
    file://0008-ipa-rkisp1-Add-tuning-file-for-imx708.patch \
    file://0009-ipa-rkisp1-imx708-Add-gamma-correction.patch \
    file://0010-ipa-rkisp1-imx708-Add-CCM-tuning.patch \
    file://0011-ipa-rkisp1-imx708-Populate-AGC-tuning.patch \
    file://0012-ipa-rkisp1-imx708-Populate-AWB-tuning-parameters.patch \
    file://0013-utils-rkisp1-Add-a-script-to-port-LSC-tables.patch \
    file://0014-ipa-rkisp1-imx708-Add-LSC-tables-from-VC4-tuning-fil.patch \
    file://0015-ipa-rkisp1-imx219-Regenerate-LSC-tables-from-VC4-tun.patch \
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
