#!/bin/sh
# Enable or disable the RWDT hardware watchdog of R-Car V4H.
#
#   rwdt-watchdog start: Allow the RWDT reset in the RST block, then let the
#                        systemd runtime watchdog serve /dev/watchdog0.
#   rwdt-watchdog stop:  Stop the systemd runtime watchdog.
#
# The RST mask is restored on every power-on reset, so "start" must run on
# every boot. Writing needs the magic word 0xA55A in bit31:16. Bit1:0 = 00
# allows the reset.

RST_ADDR=0xe6160010
RST_UNMASK=0xa55a8000
# Hardware timeout of the watchdog in microseconds (30 seconds)
RUNTIME_WATCHDOG_USEC=30000000

set_runtime_watchdog() {
    busctl set-property org.freedesktop.systemd1 /org/freedesktop/systemd1 \
        org.freedesktop.systemd1.Manager RuntimeWatchdogUSec t "$1"
}

case "$1" in
start)
    busybox devmem "${RST_ADDR}" 32 "${RST_UNMASK}" || exit 1

    v=$(busybox devmem "${RST_ADDR}" 32) || exit 1
    if [ $((v & 0x3)) -ne 0 ]; then
        echo "RWDT reset still masked (${v})" >&2
        exit 1
    fi
    echo "RWDT reset enabled (${v})"

    set_runtime_watchdog "${RUNTIME_WATCHDOG_USEC}"
    ;;
stop)
    # Keep the watchdog armed while the system is shutting down. Do nothing
    # if the state is unknown.
    case "$(systemctl is-system-running 2>/dev/null)" in
    running|degraded) ;;
    *) exit 0 ;;
    esac

    set_runtime_watchdog 0
    ;;
*)
    echo "Usage: $0 {start|stop}" >&2
    exit 2
    ;;
esac
