#!/bin/bash -eu

SCRIPT_DIR=$(cd `dirname $0` && pwd)
TARGETS=$(ls ${SCRIPT_DIR}/conf/kas/ | grep -v common.yaml)
KAS_YAML_DIR=${SCRIPT_DIR}/conf/kas/
KAS_OPTION_DIR=${SCRIPT_DIR}/conf/kas/option
export KAS_WORK_DIR=kas-work
TARGET=""
OPTIONS=""
IS_BUILD_SDK="no"

Usage () {
    echo "Usage: $0 <target>"
    echo ""
    echo "target:"
    for item in ${TARGETS[@]}; do
    echo "  - ${item%.*}"
    done
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --minimal)
            TARGET=minimal ;;
        --weston)
            TARGET=weston ;;
        -h|--help)
            Usage; exit 0 ;;
        -s|--sdk)
            IS_BUILD_SDK=yes  ;;
        -q|--quiet)
            IS_QUIET_BUILD=yes ;;
        --sbom)
            OPTIONS+=":${KAS_OPTION_DIR}/enable-sbom.yaml" ;;
        --rm-work)
            OPTIONS+=":${KAS_OPTION_DIR}/rm-work.yaml" ;;
        --sstate-mirror)
            OPTIONS+=":${KAS_OPTION_DIR}/enable-sstate-mirror.yaml" ;;
        --kernel-version)
            OPTIONS+=":${KAS_OPTION_DIR}/kernel-$2.yaml"
            shift ;;
        *) ;; # Ignore unknown option
    esac
    shift
done

if [[ "${TARGET}" == "" ]]; then
    Usage; exit;
fi

mkdir -p ${KAS_WORK_DIR}
kas build --update ${KAS_YAML_DIR}/${TARGET}.yaml${OPTIONS}

if [[ "${IS_BUILD_SDK}" == "yes" ]]; then
    kas build --update ${KAS_YAML_DIR}/${TARGET}.yaml${OPTIONS} \
        --target core-image-${TARGET}-sdk --task populate_sdk
fi

