# Rogue DDK KM build configuration

export PVR_BUILD_DIR = "r8a779g_linux"
export RGX_BVNC = "30.3.408.101"
export WINDOW_SYSTEM = "lws-generic"

# DISPLAY_CONTROLLER is overriden so that the DDK does not build a
# display driver, relying on the one aleady installed on the platform
# (e.g. "virtio_gpu" in the case of QEMU for ARM64).
# If preferred, DISPLAY_CONTROLLER can be set to build a display driver
# integrated into the DDK, such as "drm_nulldisp", but additional work
# may be needed for the X Server and Weston, to use the preferred
# display driver, if there is more than one to choose from.

export DISPLAY_CONTROLLER = ""

# The No Hardware DDK has PDump enabled by default. Disable it, to make it
# more closely resemble a hardware platform. Note that enabling PDump may
# may require modifications to the "rogueum" recipe. For example, some
# PDump scripts require Python, so this would have to be added as a
# dependency.

export PDUMP = "0"
