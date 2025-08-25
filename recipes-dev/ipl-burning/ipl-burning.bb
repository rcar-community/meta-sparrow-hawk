DESCRIPTION = "IPL burning tool"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit deploy
DEPENDS:append = "unzip-native python3-native"
do_compile[depends] += "u-boot:do_deploy"

COMPATIBLE_MACHINE = "sparrow-hawk"

ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dev = "1"
ALLOW_EMPTY:${PN}-staticdev = "1"

SRC_URI:append = " \
    file://burn.py \
    file://ipl_burning.json \
    file://ipl_burning.py \
    file://run.bat \
    file://run.sh \
    file://Flash_writer_sparrow_hawk_CR52.mot \
"

# do_configure() nothing
do_configure[noexec] = "1"
# do_compile() nothing
do_compile[noexec] = "1"
# do_install() nothing
do_install[noexec] = "1"

# Wait for U-Boot
do_deploy[depends] += "u-boot:do_deploy"

do_deploy() {
    # Create deploy folder
    install -d ${DEPLOYDIR}/${PN}

    # Copy license file to distribute
    install -m 0644 ${COMMON_LICENSE_DIR}/MIT ${DEPLOYDIR}/${PN}/MIT
    install -m 0644 ${COMMON_LICENSE_DIR}/BSD-3-Clause ${DEPLOYDIR}/${PN}/FlashWriter_LICENSE

    # Copy to deploy folder
    install -m 0644 ${WORKDIR}/burn.py ${DEPLOYDIR}/${PN}
    install -m 0644 ${WORKDIR}/ipl_burning.py ${DEPLOYDIR}/${PN}
    install -m 0644 ${WORKDIR}/ipl_burning.json ${DEPLOYDIR}/${PN}
    install -m 0755 ${WORKDIR}/run.sh ${DEPLOYDIR}/${PN}
    install -m 0644 ${WORKDIR}/run.bat ${DEPLOYDIR}/${PN}
    install -m 0644 ${WORKDIR}/Flash_writer_sparrow_hawk_CR52.mot ${DEPLOYDIR}/${PN}
    install -m 0644 ${DEPLOY_DIR}/images/${MACHINE}/flash.bin ${DEPLOYDIR}/${PN}

    # install embedded python binary for Windows environment
    PYTHON_DIR=${DEPLOYDIR}/${PN}/python-embed-amd64
    cd ${WORKDIR}
    wget -qc https://www.python.org/ftp/python/3.13.4/python-3.13.4-embed-amd64.zip
    wget -qc https://bootstrap.pypa.io/get-pip.py
    install -d ${PYTHON_DIR}
    unzip -qo ${WORKDIR}/python-3.13.4-embed-amd64.zip -d ${PYTHON_DIR}
    sed -i ${PYTHON_DIR}/*._pth -e "s/.*import site/import site/"
    install -m 0644 ${WORKDIR}/get-pip.py ${PYTHON_DIR}
    echo "./python-embed-amd64/LICENSE.txt" > ${DEPLOYDIR}/${PN}/PYTHON_LICENSE
}

addtask deploy before do_build after do_compile

