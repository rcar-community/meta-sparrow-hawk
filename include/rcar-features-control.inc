USE_WAYLAND_IVI_SHELL = \
    "${@'1' if 'ivi-shell' in '${DISTRO_FEATURES}' and \
    'wayland' in '${DISTRO_FEATURES}' else '0'}"
