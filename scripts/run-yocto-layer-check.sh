#!/bin/bash

SCRIPT_DIR=$( cd $(dirname $0) && pwd)
REPO_TOP=${SCRIPT_DIR}/../
LAYER_TOP=${REPO_TOP}/build/
TEMP_DIR=$(mktemp -d)
CURRENT_COMMIT_HASH=$(git -C ${REPO_TOP} show --stat | head -1 | awk '{print $2}')
cd ${TEMP_DIR}

# Check layer
if [[ ! -e "${LAYER_TOP}/poky" ]] || [[ ! -e "${LAYER_TOP}/meta-openembedded" ]]; then
    git clone https://git.yoctoproject.org/poky --depth=1 --branch scarthgap
    git clone https://git.openembedded.org/meta-openembedded --depth=1 --branch scarthgap
    LAYER_TOP=${TEMP_DIR}
fi

. ${LAYER_TOP}/poky/oe-init-build-env ${TEMP_DIR}
git -C ${REPO_TOP} worktree add ${TEMP_DIR}/meta-sparrow-hawk ${CURRENT_COMMIT_HASH}


yocto-check-layer --machines sparrow-hawk \
    --no-auto-dependency --dependency \
    ${LAYER_TOP}/poky/meta \
    ${LAYER_TOP}/poky/meta-poky \
    ${LAYER_TOP}/poky/meta-yocto-bsp \
    ${LAYER_TOP}/meta-openembedded/meta-oe/ \
    ${LAYER_TOP}/meta-openembedded/meta-networking/ \
    ${LAYER_TOP}/meta-openembedded/meta-multimedia/ \
    ${LAYER_TOP}/meta-openembedded/meta-python/ \
    ${TEMP_DIR}/meta-sparrow-hawk/meta-sparrow-hawk-images \
    -- ${TEMP_DIR}/meta-sparrow-hawk

# cleanup
git -C ${REPO_TOP} worktree remove ${TEMP_DIR}/meta-sparrow-hawk
#git -C ${REPO_TOP} branch -d meta-sparrow-hawk
cd ${SCRIPT_DIR}
rm -rf ${TEMP_DIR}
