DESCRIPTION = "IPL burning tool"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/LICENSE.MIT;md5=e7d4fc574e1858d0f946f9aa32397c5a"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy
DEPENDS:append = "unzip-native python3-native"
do_compile[depends] += "u-boot:do_deploy"

COMPATIBLE_MACHINE = "sparrow-hawk"

ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dev = "1"
ALLOW_EMPTY:${PN}-staticdev = "1"

PCIE_FIRMWARE = "https://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git/plain/rcar_gen4_pcie.bin;md5sum=293bdf19d8e16d3c4d8179e438db921b"
PCIE_FIRMWARE_LIC = "https://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git/plain/LICENCE.r8a779g_pcie_phy;md5sum=0b20e76a9a004b83c4a1c87e2153bbad"

SRC_URI:append = " \
    file://burn.py \
    file://ipl_burning.json \
    file://ipl_burning.py \
    file://run.bat \
    file://run.sh \
    file://Flash_writer_sparrow_hawk_CR52.mot \
    file://LICENSE-index.txt \
    file://LICENSE.MIT \
    file://LICENSE.BSD-3-Clause \
    ${PCIE_FIRMWARE} \
    ${PCIE_FIRMWARE_LIC} \
"

S = "${UNPACKDIR}"

# do_configure() nothing
do_configure[noexec] = "1"
# do_compile() nothing
do_compile[noexec] = "1"
# do_install() nothing
do_install[noexec] = "1"

# Wait for U-Boot
do_deploy[depends] += "u-boot:do_deploy"

# Wait for U-Boot licenase generation
do_deploy[depends] += "u-boot:do_populate_lic"
MACHINE_LIC = "${@d.getVar('MACHINE').replace('-', '_')}"

do_deploy() {
    # Create deploy folder
    install -d ${DEPLOYDIR}/${PN}

    # Copy license file to distribute
    install -d ${DEPLOYDIR}/${PN}/License
    install -m 0644 ${UNPACKDIR}/LICENSE-index.txt ${DEPLOYDIR}/${PN}
    install -m 0644 ${UNPACKDIR}/LICENSE.MIT ${DEPLOYDIR}/${PN}/License
    install -m 0644 ${UNPACKDIR}/LICENSE.BSD-3-Clause ${DEPLOYDIR}/${PN}/License

    # Copy to deploy folder
    install -m 0644 ${UNPACKDIR}/burn.py ${DEPLOYDIR}/${PN}
    install -m 0644 ${UNPACKDIR}/ipl_burning.py ${DEPLOYDIR}/${PN}
    install -m 0644 ${UNPACKDIR}/ipl_burning.json ${DEPLOYDIR}/${PN}
    install -m 0755 ${UNPACKDIR}/run.sh ${DEPLOYDIR}/${PN}
    install -m 0644 ${UNPACKDIR}/run.bat ${DEPLOYDIR}/${PN}
    install -m 0644 ${UNPACKDIR}/Flash_writer_sparrow_hawk_CR52.mot ${DEPLOYDIR}/${PN}
    install -m 0644 ${DEPLOY_DIR}/images/${MACHINE}/flash.bin ${DEPLOYDIR}/${PN}
    cp -r  ${DEPLOY_DIR}/licenses/${MACHINE_LIC}/u-boot ${DEPLOYDIR}/${PN}/License/u-boot_licenses
    install -m 755 ${UNPACKDIR}/rcar_gen4_pcie.bin ${DEPLOYDIR}/${PN}
    install -m 755 ${UNPACKDIR}/LICENCE.r8a779g_pcie_phy ${DEPLOYDIR}/${PN}/License

    # install embedded python binary for Windows environment
    PYTHON_DIR=${DEPLOYDIR}/${PN}/python-embed-amd64
    cd ${UNPACKDIR}
    wget -qc https://www.python.org/ftp/python/3.13.4/python-3.13.4-embed-amd64.zip
    wget -qc https://bootstrap.pypa.io/get-pip.py
    install -d ${PYTHON_DIR}
    unzip -qo ${UNPACKDIR}/python-3.13.4-embed-amd64.zip -d ${PYTHON_DIR}
    sed -i ${PYTHON_DIR}/*._pth -e "s/.*import site/import site/"
    install -m 0644 ${UNPACKDIR}/get-pip.py ${PYTHON_DIR}
}

addtask deploy before do_build after do_compile

