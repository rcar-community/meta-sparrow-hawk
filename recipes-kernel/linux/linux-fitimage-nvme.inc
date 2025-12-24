DEPENDS += "initramfs-image"
FIT_ITS_FILE = "fit-image.its.nvme"

do_compile[depends] += "initramfs-image:do_image_complete"
