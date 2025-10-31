FILESEXTRAPATHS:prepend:rcar-gen4 := "${THISDIR}/${PN}:"

SRC_URI:append:rcar-gen4 = " file://drm-backend-remove-gbm-version-check.patch"

DEPENDS:append:rcar-gen4= " libgbm"

RDEPENDS:${PN}:append:rcar-gen4 = " libgbm"

RDEPENDS:${PN}-examples:append:rcar-gen4 = " libgbm"

PACKAGECONFIG:remove:virtclass-multilib-lib32 = "launch"

EXTRA_OEMESON:append:rcar-gen4 = " -Dsimple-clients=egl,shm,damage,im,touch"
