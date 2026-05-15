require recipes-core/images/core-image-minimal.bb
require core-image-minimal.inc

DESCRIPTION = "Image that includes everything within core-image-minimal \
plus meta-toolchain, development headers and libraries to form a standalone SDK."

IMAGE_FEATURES += "dev-pkgs tools-sdk \
	tools-debug eclipse-debug tools-profile tools-testapps debug-tweaks ssh-server-openssh"

IMAGE_INSTALL += "kernel-devsrc"

# Compiling stuff, specifically SystemTap probes, can require lots of memory
# See https://bugzilla.yoctoproject.org/show_bug.cgi?id=14673
QB_MEM = "-m 768"
