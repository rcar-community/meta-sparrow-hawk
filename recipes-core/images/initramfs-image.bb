DESCRIPTION = "Initramfs image to load PCIe module at early timing"
LICENSE = "MIT"
inherit core-image

IMAGE_FEATURES = ""
IMAGE_INSTALL = "kernel-module-pcie-rcar-gen4 sparrow-hawk-fw busybox"
IMAGE_INSTALL:remove = "snort"
IMAGE_INSTALL:remove = "smcroute"
IMAGE_INSTALL:remove = "iperf3"
IMAGE_INSTALL:remove = "ethtool"
IMAGE_INSTALL:remove = "coreutils"
IMAGE_INSTALL:remove = "tcpdump"

INITRAMFS_IMAGE = "initramfs-image"
INITRAMFS_IMAGE_BUNDLE = "0"
IMAGE_NAME = "uInitramfs"
IMAGE_FSTYPES = "cpio.gz"

ROOTFS_POSTPROCESS_COMMAND += "copy_init_script;"
copy_init_script() {
    install -m 0755 ${THISDIR}/files/init ${IMAGE_ROOTFS}/init
}

ROOTFS_POSTPROCESS_COMMAND += "remove_kernel_image;"
remove_kernel_image() {
    rm -f ${IMAGE_ROOTFS}/boot/*
}

ROOTFS_POSTPROCESS_COMMAND += "create_mount_dirs;"
create_mount_dirs() {
    mkdir -p ${IMAGE_ROOTFS}/proc
    mkdir -p ${IMAGE_ROOTFS}/dev
    mkdir -p ${IMAGE_ROOTFS}/sys
    mkdir -p ${IMAGE_ROOTFS}/mnt
}

