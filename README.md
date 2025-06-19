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
    layers: meta-oe, meta-python, meta-networking
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

* Then, using one of three way to build Image. (Manual Build, Using template configuration, Using Renesas'script)

## Manual Build

* Create a directory as working folder

```bash
    $ mkdir build
    $ cd build
    $ export WORK=`pwd`
```

* Clone required layers

```bash
    $ cd $WORK
    $ git clone git://git.yoctoproject.org/poky
    $ git clone git://git.openembedded.org/meta-openembedded
    $ git clone https://github.com/rcar-community/meta-sparrow-hawk.git
```

* Switch to proper branches/commits

```bash
    $ cd $WORK
    $ git -C poky checkout -b scarthgap origin/scarthgap
    $ git -C meta-openembedded checkout -b scarthgap origin/scarthgap
    $ git -C meta-sparrow-hawk checkout -b scarthgap origin/scarthgap
```

* Initialize a build using the 'oe-init-build-env' script in Poky. e.g.:

```bash
    $ cd $WORK
    $ TEMPLATECONF=${WORK}/meta-sparrow-hawk/conf/templates/sparrow-hawk source poky/oe-init-build-env build_sparrow_hawk
```

* For a list of sample local.conf file, please refer to: [conf/templates/](conf/templates/)

* Build the target file system image using bitbake:

```bash
    $ bitbake core-image-minimal
```

After completing the images for the target machine will be available in the
output directory 'tmp/deploy/images/sparrow-hawk'.

Images generated:

* Image (generic Linux Kernel binary image file)

* \<SoC\>-\<machine name\>.dtb (DTB for target machine)

* core-image-minimal-\<machine name\>.tar.bz2 (rootfs tar+bzip2)

* core-image-minimal-\<machine name\>.ext4  (rootfs ext4 format)

## Using build script

```
bash -c "$(wget -O- https://raw.github.com/rcar-community/meta-sparrow-hawk/scarthgap/build.sh)"
```

## Build Instructions for SDK

NOTE:

**This may be changed in the near feature. These instructions are tentative.**

Should define the staticdev in SDK image feature for installing the static libs
to SDK in local.conf.

```bash
    SDKIMAGE_FEATURES:append = " staticdev-pkgs"
```


### For 64-bit target SDK (aarch64)

Use `bitbake -c populate_sdk` for generating the toolchain SDK

```bash
    $ bitbake core-image-minimal -c populate_sdk
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


## R-Car Information

Refer to the following for more information from eLinux website

- https://elinux.org/R-Car
- https://elinux.org/R-Car/Boards/SparrowHawk

