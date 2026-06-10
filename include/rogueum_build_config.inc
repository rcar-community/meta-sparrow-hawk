# Rogue DDK UM build configuration

require include/roguekm_build_config.inc

# Override the default DRM driver settings in the DDK, to suit the DRM
# display driver used by QEMU ("virtio_gpu" for ARM64). Note that the
# "bochs-drm" driver, used by the x86 version of QEMU, is not suitable
# for use with the IMG DDK, as it has no PRIME support.

PVR_DRM_MODESET_DRIVER_NAME = "rcar-du"
PVR_DRM_MODESET_MODULE_NAME = "dumb"

# Some window systems require Rogue UM to be built with active flush enabled.

SUPPORT_ACTIVE_FLUSH_DISTRO = "${@bb.utils.contains('DISTRO_FEATURES', 'surfaceless', '1', '0', d)}"
SUPPORT_ACTIVE_FLUSH_DISTRO += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '1', '0', d)}"

export SUPPORT_ACTIVE_FLUSH = "${@bb.utils.contains('SUPPORT_ACTIVE_FLUSH_DISTRO', '1', '1', '0', d)}"

# Including "unittests", "scripts", "servicestools" and "hwperftools" in
# EXCLUDED_APIS, excludes some unittests, scripts and PVR Services tools
# that are built by default (see rogue/build/linux/common/apis/unittests.mk).
# If any of these are required, they can be included by modifying the rogueum
# recipe, and adding the required components to the EXTRA_COMPONENTS variable.
# Alternatively, EXCLUDED_APIS can be modified to not exclude the required
# groups.
# Note that either of the above changes may require modifying the rogueum
# recipe (e.g. to include additional dependencies, such as Python).

export EXCLUDED_APIS = "opengl \
                        unittests scripts servicestools hwperftools rogue2d"
