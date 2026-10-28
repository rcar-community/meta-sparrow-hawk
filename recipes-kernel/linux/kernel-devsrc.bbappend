# kernel-devsrc sets S to STAGING_KERNEL_DIR and do_unpack is noexec.
# However, do_create_spdx (create-spdx with SPDX_INCLUDE_SOURCES = "1")
# calls do_unpack directly and cleandirs of do_unpack and base_do_unpack
# removes ${S}.
# This empties work-shared/<machine>/kernel-source and breaks the build of
# the kernel modules which run after that.
do_unpack[cleandirs] = ""
base_do_unpack[cleandirs] = ""
