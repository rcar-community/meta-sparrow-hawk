#!/bin/bash

RESIZE_CHECK_FILE=/etc/.partition_resized

if [[ ! -e ${RESIZE_CHECK_FILE} ]]; then
    echo "This script expands rootfs to fully use whole storage."
    echo "if you want to stop this, please exit with CTRL+C"
    echo "Script will contiune after 15 seconds..."
    sleep 15s

    ROOT_DEV=$(mount |head -1|cut -d' ' -f1)
    DEV=$(echo ${ROOT_DEV} | sed -e 's/p//' -e 's/[0-9]$//')
    PARTITON_NUM=$(dmesg | grep root= | sed -e "s|.*/dev/.*[0-9]p||" -e "s| .*||")

    parted ${DEV} resizepart ${PARTITON_NUM} 100%
    resize2fs ${ROOT_DEV}
    touch ${RESIZE_CHECK_FILE}
    echo "Expanding rootfs is finished."
    echo "If possbile, please reboot the device."
else
    echo "It seems rootfs has been extended."
    echo;
    echo " if you want to force execution this script, please remove '${RESIZE_CHECK_FILE}' ."
fi

