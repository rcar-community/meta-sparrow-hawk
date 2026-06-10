# Incorporate IMG changes into Mesa

require include/rogueum_build_config.inc

FILESEXTRAPATHS:append := "${THISDIR}/files:"

SRC_URI += "file://0001-Add-PVR-Gallium-driver.patch"
SRC_URI += "file://0002-dri2-add-a-driver-compatibility-check-extension.patch"
SRC_URI += "file://0003-dri-Add-some-new-DRI-formats-and-fourccs.patch"
SRC_URI += "file://0004-GL_EXT_sparse_texture-entry-points.patch"
SRC_URI += "file://0005-Add-support-for-various-GLES-extensions.patch"
SRC_URI += "file://0006-Add-EGL_IMG_cl_image-extension.patch"
SRC_URI += "file://0007-egl-optimise-eglMakeCurrent-for-the-case-where-nothi.patch"
SRC_URI += "file://0008-GL_EXT_shader_pixel_local_storage2-entry-points.patch"
SRC_URI += "file://0009-GL_IMG_framebuffer_downsample-entry-points.patch"
SRC_URI += "file://0010-GL_OVR_multiview-entry-points.patch"
SRC_URI += "file://0011-Add-OVR_multiview_multisampled_render_to_texture.patch"
SRC_URI += "file://0012-wayland-drm-install-wayland-drm.xml-to-the-configure.patch"
SRC_URI += "file://0013-Enable-buffer-sharing-in-the-kms_swrast-driver.patch"
SRC_URI += "file://0014-egl-wayland-add-support-for-RGB565-back-buffers.patch"
SRC_URI += "file://0015-egl-wayland-post-maximum-damage-when-blitting.patch"
SRC_URI += "file://0016-egl-wayland-flush-the-swap-buffers-blit.patch"
SRC_URI += "file://0017-dri-use-a-supported-API-in-driCreateNewContext.patch"
SRC_URI += "file://0018-gbm-add-gbm_bo_blit.patch"
SRC_URI += "file://0019-gbm-don-t-assert-if-DRI-context-creation-fails.patch"
SRC_URI += "file://0020-egl-wayland-add-pbuffer-support.patch"
SRC_URI += "file://0021-GL_EXT_multi_draw_indirect-entry-points.patch"
SRC_URI += "file://0022-dri-add-support-for-YUV-DRI-config.patch"
SRC_URI += "file://0023-egl-add-support-for-EXT_yuv_surface.patch"
SRC_URI += "file://0024-dri-add-missing-__DRI_IMAGE_COMPONENTS-define-for-EG.patch"
SRC_URI += "file://0025-egl-wayland-expose-EXT_yuv_surface-support.patch"
SRC_URI += "file://0026-gbm-add-some-new-GBM-formats.patch"
SRC_URI += "file://0027-egl-add-null-platform.patch"
SRC_URI += "file://0028-egl-add-support-for-EXT_image_gl_colorspace.patch"
SRC_URI += "file://0029-meson-force-C-2011-for-thread_local.patch"
SRC_URI += "file://0030-dri2-add-support-for-swap-intervals-other-than-1.patch"
SRC_URI += "file://0031-null_platform-add-support-for-explicit-synchronisati.patch"
SRC_URI += "file://0032-egl-null-add-support-for-DRM-image-format-modifiers.patch"
SRC_URI += "file://0033-egl-query-the-supported-ES2-context-version.patch"
SRC_URI += "file://0034-meson-allow-libGL-to-be-built-without-GLX.patch"
SRC_URI += "file://0035-egl-wayland-process-non-resized-window-movement.patch"
SRC_URI += "file://0036-Separate-EXT_framebuffer_object-from-ARB-version.patch"
SRC_URI += "file://0037-egl-null-add-support-for-async-flip-with-front-buffe.patch"
SRC_URI += "file://0038-gbm-add-pbuffer-support.patch"
SRC_URI += "file://0039-egl-null-expose-EXT_yuv_surface-support.patch"
SRC_URI += "file://0040-dri-allow-drivers-to-obtain-the-display-GPU-FD.patch"
SRC_URI += "file://0041-egl-drm-add-support-for-DRI_PRIME-GPU-selection.patch"
SRC_URI += "file://0042-egl-null-add-support-for-DRI_PRIME-GPU-selection.patch"
SRC_URI += "file://0043-egl-null-introduce-NULL_DRM_DISPLAY.patch"
SRC_URI += "file://0044-vulkan-wsi-check-the-DRI3-and-Present-XCB-reply-poin.patch"
SRC_URI += "file://0045-vulkan-wsi-make-the-display-FD-available.patch"
SRC_URI += "file://0046-pvr-wsi-add-PowerVR-Vulkan-WSI-library.patch"
SRC_URI += "file://0047-vulkan-wsi-default-to-force_bgra8_unorm_first-true.patch"
SRC_URI += "file://0048-vulkan-wsi-enable-additional-formats-for-Display.patch"
SRC_URI += "file://0049-mesa-partially-revert-pbuffer-attribute-removal.patch"
SRC_URI += "file://0050-egl_dri2-set-pbuffer-config-attribs-to-0-for-non-pbu.patch"
SRC_URI += "file://0051-GL_ARB_geometry_shader4-entry-points.patch"
SRC_URI += "file://0052-egl-wayland-add-EGL_BUFFER_PRESERVED-support.patch"
SRC_URI += "file://0053-glapi-restore-exec-dynamic.patch"
SRC_URI += "file://0054-GL_NV_draw_instanced-and-GL_NV_instanced_arrays-entr.patch"
SRC_URI += "file://0055-vulkan-wsi-fix-headless-swapchain-memory-leaks.patch"
SRC_URI += "file://0056-vulkan-wsi-add-headless-VK_KHR_present_wait.patch"
SRC_URI += "file://0057-vulkan-wsi-use-can_present_on_device-for-Wayland.patch"
SRC_URI += "file://0058-vulkan-wsi-check-for-VK_EXT_pci_bus_info.patch"
SRC_URI += "file://0059-gallium-add-support-for-image-driver-createNewDrawab.patch"
SRC_URI += "file://0060-loader-dri3-indicate-to-driver-when-drawable-is-a-pi.patch"
SRC_URI += "file://0061-gbm-use-a-supported-API-in-createContextAttribs.patch"
SRC_URI += "file://0062-egl-wayland-always-get-server-GPU-using-wl_drm.patch"
SRC_URI += "file://0063-GL_EXT_texture_storage_compression-entry-points.patch"
SRC_URI += "file://0064-gallium-pvr-support-protected-content-and-surface.patch"
SRC_URI += "file://0065-egl-wayland-allow-sharing-of-protected-buffers.patch"
SRC_URI += "file://0066-vulkan-wsi-display-inherit-on-screen-framebuffer.patch"
SRC_URI += "file://0067-gbm-add-the-YVYU-UYVY-and-VYUY-formats.patch"
SRC_URI += "file://0068-mapi-add-EXT_texture_storage-entry-points.patch"
SRC_URI += "file://0069-egl-Use-gbm_bo_create_with_modifiers2-when-the-surfa.patch"
SRC_URI += "file://0070-egl-drm-Allow-surfaces-with-protected-content-to-hav.patch"

SRC_URI += "${@bb.utils.contains('PVR_DRM_MODESET_DRIVER_NAME', 'virtio_gpu', 'file://0071-virtio-gpu-use-the-pvr-driver.patch', '', d)}"

# Backport rcar-du build error patch from DDK 25.2
SRC_URI += "file://0066-gallium-pvr-underscorify-the-PVR-alias-for-identifie.patch"


# Some files are stored in the GIT repository with LF line endings,
# but have CR/LF line endings when checked out. Patches generated for
# such files with "git format-patch" have LF line endings, which
# cannot be applied, without error, to checked out files with "patch".
# The line endings for such files are converted from CR/LF to LF prior
# to patching, and converted back afterwards.
PATCH_CRLF_FILES = "src/mesa/main/formats.csv"

crlf_file_prepatch() {
	local rf
	local af

	for rf in "${PATCH_CRLF_FILES}"
	do
		af=${S}/${rf}
		sed 's/\r$//' < ${af} > ${af}.lf && mv -f ${af}.lf ${af}
	done
}

crlf_file_postpatch() {
	local rf
	local af

	for rf in "${PATCH_CRLF_FILES}"
	do
		af=${S}/${rf}
		sed 's/$/\r/' < ${af} > ${af}.crlf && mv -f ${af}.crlf ${af}
	done
}

do_patch[prefuncs] += "crlf_file_prepatch"
do_patch[postfuncs] += "crlf_file_postpatch"

PACKAGECONFIG:append:class-target = " pvr-alias"

GALLIUMDRIVERS:append:class-target = ",pvr"

PACKAGECONFIG[pvr-alias] = "-Dgallium-pvr-alias=${PVR_DRM_MODESET_DRIVER_NAME}"

# The DDK integration example uses the virtio-gpu display driver, which
# would normally be associated with the virgl GPU driver.
PACKAGECONFIG:remove = "virgl"

PACKAGECONFIG:append:class-target = " pvr-vulkan-display"

VULKAN_DRIVERS:append:class-target = ",pvr"

PACKAGECONFIG[pvr-vulkan-display] = "-Dpvr-vulkan-display=${PVR_DRM_MODESET_DRIVER_NAME}"

PLATFORMS:append = " ${@bb.utils.filter('DISTRO_FEATURES', 'null', d)}"

NO_GLX = "${@bb.utils.contains('EXCLUDED_APIS', 'opengl', 'disabled', 'null',d)} "

PACKAGECONFIG[x11] = ",-Dglx=${NO_GLX},${X11_DEPS}"

FILES:mesa-vulkan-drivers:append = " ${libdir}/libpvr_mesa_wsi.so"

FILES:libegl-mesa-dev:append = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' ${datadir}/pkgconfig/wayland-drm.pc', '', d)}"
