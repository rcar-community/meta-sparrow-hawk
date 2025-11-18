# OpenEmbedded/Yocto BSP layer for Sparrow Hawk

This layer provides support for Sparrow Hawk for use with OpenEmbedded and/or Yocto.

## Original BSP image

* core-image-minimal


## Contribution

- Under consideration

## Layer Dependencies

This layer depends on:

* poky

```bash
    URI: git://git.yoctoproject.org/poky
    layers: meta, meta-poky, meta-yocto-bsp
    branch: scarthgap
```

* meta-openembedded

```bash
    URI: git://git.openembedded.org/meta-openembedded
    layers: meta-oe, meta-python, meta-networking, meta-multimedia
    branch: scarthgap
```


# Build Instructions

## Required Environment

* Refer to https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html to prepare Build Host.

* This also needs git user name and email defined:

```bash
   $ git config --global user.email "you@example.com"
   $ git config --global user.name "Your Name"
```

## Build using build script

```bash
git clone https://github.com/rcar-community/meta-sparrow-hawk -b scarthgap
cd meta-sparrow-hawk
./build.sh
```

build option can be confirmed by -h option:
```bash
./build.sh -h
```

### Integrate PCIe firmware into rootfs

If you have pcie firmware binary and want to integrate it into rootfs,
Please copy `rcar_gen4_pcie.bin` into "meta-sparrow-hawk/firmware/".
Please see also https://elinux.org/R-Car/Boards/WhiteHawk#PCIe_firmware

## Build Instructions for SDK

```bash
./build.sh --sdk
```

The SDK can be found in the output directory `tmp/deploy/sdk`

* `poky-glibc-x86_64-core-image-minimal-aarch64-<machine name>-toolchain-x.x.sh`


### Usage of toolchain SDK

Install the SDK to the default: `/opt/poky/x.x`

* For 64-bit target SDK

```bash
    $ sh poky-glibc-x86_64-core-image-minimal-aarch64-<machine name>-toolchain-x.x.sh
```

* For 64-bit application, using environment script in `/opt/poky/x.x`

```bash
    $ source /opt/poky/x.x/environment-setup-aarch64-poky-linux
```

## Reference

- R-Car Community site
  - https://rcar-community.github.io/
- Sparrow Hawk board page
  - https://rcar-community.github.io/Sparrow-Hawk/index.html

