FILESEXTRAPATHS:prepend:rcar-gen4 := "${THISDIR}/${PN}:"

SRC_URI:append:rcar-gen4 = " file://drm-backend-remove-gbm-version-check.patch"

SRC_URI:append:rcar-gen4 = " file://0001-libweston-reduce-checks-for-dmabufs-with-DRM-modifie.patch"
SRC_URI:append:rcar-gen4 = " file://0002-backend-drm-allow-linear-framebuffers-if-no-KMS-modi.patch"
SRC_URI:append:rcar-gen4 = " file://0003-desktop-shell-fix-segfault-in-end_busy_cursor.patch"
SRC_URI:append:rcar-gen4 = " file://0004-clients-simple-dmabuf-egl-make-buffers-read-write.patch"

PACKAGECONFIG:remove:virtclass-multilib-lib32 = "launch"

EXTRA_OEMESON:append:rcar-gen4 = " -Dsimple-clients=egl,shm,damage,im,touch"
