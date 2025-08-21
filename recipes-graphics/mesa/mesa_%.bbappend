# These below libraries are provided by user-module-gles
PACKAGECONFIG:remove:rcar-gen4 = "${@bb.utils.contains('MACHINE_FEATURES', 'gsx', 'egl gles', '', d)}"

do_install:append:rcar-gen4() {
    # Have to remove khrplatform.h file due to conflict with user-module-gles
    # even though libegl from mesa is removed
    if [ "${MACHINE_FEATURES}" =~ "gsx" ]; then
        rm -rf ${D}${includedir}/KHR
    fi
}
