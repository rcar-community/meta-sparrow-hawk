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
PV = "v0.7.2+upstream+git${SRCPV}"
SRC_URI = "git://gitlab.freedesktop.org/camera/libcamera.git;protocol=https;branch=master"
# nooelint: oelint.file.upstreamstatus oelint.file.patchsignedoff
SRC_URI:append = " \
    file://0001-ipa-libipa-agc-Keep-frame-duration-limits-ordered.patch \
    file://0002-ipa-libipa-agc-Retrieve-FrameDurationLimits-earlier.patch \
    file://0003-ipa-libipa-agc-Report-FrameDurationLimits.patch \
    file://0004-ipa-libipa-agc-Calculate-vblank-and-frame-duration-s.patch \
    file://0005-ipa-libipa-agc-Take-parameters-from-active-state-for.patch \
    file://0006-libcamera-pipeline-Set-vblank-on-more-platforms.patch \
    file://0007-ipa-libipa-agc-Rework-frame-duration-limit-calculati.patch \
    file://0008-ipa-camera_sensor_helper-Introduce-exposure-margin.patch \
    file://0009-ipa-libipa-agc-Take-exposure-margin-into-account.patch \
    file://0010-ipa-libipa-fixedpoint-Shift-unsigned-type-for-scalin.patch \
    file://0011-ipa-libipa-quantized-Make-floating-point-type-custom.patch \
    file://0012-ipa-libipa-quantized-Use-double-when-necessary.patch \
    file://0013-ipa-libipa-awb-Log-rgb-means-immediately.patch \
    file://0014-ipa-libipa-awb_bayes-Fix-initial-max-value.patch \
    file://0015-libcamera-controls-Fix-ColourTemperature-direction.patch \
    file://0016-ipa-libipa-gamma-Static-assert-lookup-node-count.patch \
    file://0017-ipa-libipa-gamma-Accept-const-segment-lengths.patch \
    file://0018-ipa-libipa-gamma-Use-std-optional-for-segment-length.patch \
    file://0019-include-linux-Update-to-Linux-7.3-rc3.patch \
    file://0020-libcamera-pipeline-Add-R-Car-Gen4-ISP-pipeline.patch \
    file://0021-ipa-rppx1-Add.patch \
    file://0022-ipa-rppx1-blc-Add.patch \
    file://0023-ipa-rppx1-agc-Add.patch \
    file://0024-ipa-rppx1-awb-Add.patch \
    file://0025-ipa-rppx1-lux-Add.patch \
    file://0026-ipa-rppx1-ccm-Add.patch \
    file://0027-ipa-rppx1-goc-Add.patch \
    file://0028-ipa-rppx1-gsl-Add.patch \
    file://0029-ipa-rppx1-lsc-Add.patch \
    file://0030-ipa-rppx1-Add-uncalibrated-tuning-file.patch \
    file://0031-ipa-rppx1-Add-tuning-file-for-imx708.patch \
    file://0032-ipa-rppx1-imx708-Add-CCM-tuning.patch \
    file://0033-ipa-rppx1-imx708-Populate-AGC-tuning.patch \
    file://0034-ipa-rppx1-imx708-Populate-AWB-tuning-parameters.patch \
    file://0035-ipa-rppx1-imx708-Add-LSC-tables-from-VC4-tuning-file.patch \
    file://0036-ipa-rppx1-Add-tuning-file-for-imx219.patch \
    file://0037-ipa-rppx1-imx219-Add-basic-AGC-AWB-CCM-tuning.patch \
    file://0038-ipa-rppx1-imx219-Add-LSC-tuning.patch \
"
SRCREV = "0f0450158f4eaa37de633520822a9c4a1c25c5ea"
S = "${WORKDIR}/git"

PACKAGECONFIG[pycamera] = "-Dpycamera=enabled,-Dpycamera=disabled,python3 python3-pybind11"
PACKAGECONFIG[gst] = "-Dgstreamer=enabled,-Dgstreamer=disabled,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG:append = " gst pycamera"
PACKAGES += "${PN}-gst ${PN}-pycamera"

FILES:${PN} += "${libexecdir}/libcamera/v4l2-compat.so"
FILES:${PN}-gst += "${libdir}/gstreamer-1.0"
FILES:${PN}-pycamera += "${PYTHON_SITEPACKAGES_DIR}/libcamera"

BBCLASSEXTEND = ""
LIBCAMERA_PIPELINES = "rcar-gen4"
EXTRA_OEMESON := "\
    --prefix=/usr/ \
    -Dpipelines=${LIBCAMERA_PIPELINES} \
    -Dipas=rppx1 \
    -Dcam=enabled \
    -Dpycamera=enabled \
    -Dtest=false \
    -Ddocumentation=disabled \
"

inherit meson pkgconfig python3native

do_configure:prepend() {
    sed -i -e 's|py_compile=True,||' ${S}/utils/codegen/ipc/mojo/public/tools/mojom/mojom/generate/template_expander.py
}

