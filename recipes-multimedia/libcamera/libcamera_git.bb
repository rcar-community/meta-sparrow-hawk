SUMMARY = "Libcamera"
DESCRIPTION = "A complex camera support library for Linux, Android, and ChromeOS"
HOMEPAGE = "https://libcamera.org/"
BUGTRACKER = "https://gitlab.freedesktop.org/camera/libcamera/-/issues"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://LICENSES/GPL-2.0-or-later.txt;md5=fed54355545ffd980b814dab4a3b312c \
    file://LICENSES/LGPL-2.1-or-later.txt;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
"
CVE_PRODUCT = ""

DEPENDS = "chrpath-native gnutls libevent libyaml python3-jinja2-native python3-ply-native python3-pyyaml-native"
DEPENDS:append = " libdrm libpisp libsdl2 python3-pybind11 udev tiff"
PV = "v0.6.0+upstream+git${SRCPV}"
SRC_URI = "git://git.libcamera.org/libcamera/libcamera.git;protocol=https;branch=master"
# nooelint: oelint.file.upstreamstatus oelint.file.patchsignedoff
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
SRCREV = "f4c3dee21770b9b8817c80265b9f81eda1833731"

PACKAGECONFIG[pycamera] = "-Dpycamera=enabled,-Dpycamera=disabled,python3 python3-pybind11"
PACKAGECONFIG[gst] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG:append = " gst pycamera"
PACKAGES += "${PN}-gst ${PN}-pycamera"

FILES:${PN} += "${libexecdir}/libcamera/v4l2-compat.so"
FILES:${PN}-gst += "${libdir}/gstreamer-1.0"
FILES:${PN}-pycamera += "${PYTHON_SITEPACKAGES_DIR}/libcamera"

BBCLASSEXTEND = ""
LIBCAMERA_PIPELINES = "rcar-gen4,rkisp1"
EXTRA_OEMESON := "\
    --prefix=/usr/ \
    -Dpipelines=${LIBCAMERA_PIPELINES} \
    -Dipas=rkisp1 \
    -Dcam=enabled \
    -Dpycamera=enabled \
    -Dtest=false \
    -Ddocumentation=disabled \
"

inherit meson pkgconfig python3native

do_configure:prepend() {
    sed -i -e 's|py_compile=True,||' ${S}/utils/codegen/ipc/mojo/public/tools/mojom/mojom/generate/template_expander.py
}

