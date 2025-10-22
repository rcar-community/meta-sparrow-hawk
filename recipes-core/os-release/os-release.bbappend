TARGET_FILE="${D}${sysconfdir}/os-release"

# Disable sstate
do_install[nostamp] = "1"

do_install:append () {
    # Adding Yocto layer information
    echo "" >> ${TARGET_FILE}

    # Other layers
    for layer in `ls -d ${TOPDIR}/../poky ${TOPDIR}/../meta-*`; do
        LAYER_NAME=$(basename $layer)
        BRANCH=$(git -C ${layer} branch | grep \* | awk '{print $2}')
        COMMIT=$(git -C ${layer} rev-parse HEAD)

        # Output results if layer is used
        if grep -q ${LAYER_NAME} ${TOPDIR}/conf/bblayers.conf; then
            echo "${LAYER_NAME}=\"${BRANCH}:${COMMIT}\"" >> ${TARGET_FILE}
        fi
    done
}

