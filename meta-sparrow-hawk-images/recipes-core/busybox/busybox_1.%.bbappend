FILESEXTRAPATHS:prepend:rcar-gen4 := "${THISDIR}/${PN}:"

SRC_URI:append:rcar-gen4 = " file://0001-stty-Add-support-for-non-standard-baudrates.patch"

# Make stty's priority higher than coreutils
ALTERNATIVE_PRIORITY:rcar-gen4[stty] = "110"
