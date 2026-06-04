#!/bin/bash

SCRIPT_DIR=$( cd $(dirname $0) && pwd)
REPO_TOP=${SCRIPT_DIR}/../
TEMP_DIR=$(mktemp -d)
CURRENT_COMMIT_HASH=$(git show --stat | head -1 | awk '{print $2}')

. ${REPO_TOP}/build/poky/oe-init-build-env ${TEMP_DIR}
git -C ${REPO_TOP} worktree add ${TEMP_DIR}/meta-sparrow-hawk ${CURRENT_COMMIT_HASH}

yocto-check-layer --machines sparrow-hawk \
    --no-auto-dependency --dependency \
    ${REPO_TOP}/build/poky/meta \
    ${REPO_TOP}/build/poky/meta-poky \
    ${REPO_TOP}/build/poky/meta-yocto-bsp \
    ${REPO_TOP}/build/meta-openembedded/meta-oe/ \
    ${REPO_TOP}/build/meta-openembedded/meta-networking/ \
    ${REPO_TOP}/build/meta-openembedded/meta-multimedia/ \
    ${REPO_TOP}/build/meta-openembedded/meta-python/ \
    ${TEMP_DIR}/meta-sparrow-hawk/meta-sparrow-hawk-bsp \
    -- ${TEMP_DIR}/meta-sparrow-hawk/meta-sparrow-hawk-images

# cleanup
git -C ${REPO_TOP} worktree remove ${TEMP_DIR}/meta-sparrow-hawk
cd ${SCRIPT_DIR}
rm -rf ${TEMP_DIR}
