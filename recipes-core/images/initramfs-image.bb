DESCRIPTION = "Initramfs image to load PCIe module at early timing"
LICENSE = "MIT"
inherit image

IMAGE_FEATURES = ""
IMAGE_INSTALL_LIST = " \
    linux-firmware-pcie-rcar linux-firmware-r8a779g-pcie-phy-license \
    busybox \
"
IMAGE_INSTALL_LIST:append = " kernel-module-pcie-rcar-gen4"

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

# Force override IMAGE_INSATLL to create minimum initramfs
python __anonymous () {
    items = d.getVar("IMAGE_INSTALL_LIST").split()
    d.setVar("IMAGE_INSTALL", " ".join(items))
}

