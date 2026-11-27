#!/bin/bash
# ORT license report of the Sparrow Hawk image. See README.md.
#
#   scripts/ort/run.sh <stage>...      stages: spdx analyze scan pkgconf evaluate report   (all = every stage)
#
# Environment:
#   ORT_WORK        work directory (default ./ort-work)
#   ORT_CONFIG_DIR  checkout of the ort-config repository (default $ORT_WORK/ort-config, cloned when it is missing)
#   ORT_CONFIG_REV  revision of ort-config to use when it is cloned (default sparrow-hawk-work)
#   ORT_IMAGE       ORT docker image (default: pinned version)
#   ORT_USER        user of the docker container (default: the current user). ScanCode needs the user of the image
#                   (1000:1000) to enter /home/ort, so set ORT_USER=1000:1000 when your uid is not 1000 (GitHub runner).
#   SPDX_ROOT / IMAGE_MANIFEST   inputs of the "spdx" stage: tmp/deploy/spdx/2.2 and the image .manifest of the build
set -euo pipefail
# The container user may differ from the current user: let it write to the directories made here.
[ -n "${ORT_USER:-}" ] && umask 000
HERE=$(cd "$(dirname "$0")" && pwd)
export ORT_WORK=$(mkdir -p "${ORT_WORK:-ort-work}" && cd "${ORT_WORK:-ort-work}" && pwd)
ORT_IMAGE=${ORT_IMAGE:-ghcr.io/oss-review-toolkit/ort:98.0.0}
ORT_CONFIG_REPO=${ORT_CONFIG_REPO:-https://github.com/yhamamachi/ort-config.git}
ORT_CONFIG_DIR=${ORT_CONFIG_DIR:-$ORT_WORK/ort-config}
if [ ! -d "$ORT_CONFIG_DIR" ]; then
    git clone "$ORT_CONFIG_REPO" "$ORT_CONFIG_DIR"
    git -C "$ORT_CONFIG_DIR" checkout "${ORT_CONFIG_REV:-sparrow-hawk-work}"
fi
mkdir -p "$ORT_WORK/home" "$ORT_WORK/ortdata/tmp"
# (files made by the container are already open; only the ones of the current user can be changed)
[ -n "${ORT_USER:-}" ] && { chmod -R a+rwX "$ORT_WORK" 2>/dev/null || true; }

# ORT in docker. /w = work directory, /cfg = ort-config. ORT_DATA_DIR keeps the scanner storage (scan results, sources).
ort() {
    docker run --rm -u "${ORT_USER:-$(id -u):$(id -g)}" -v "$ORT_WORK":/w -v "$ORT_CONFIG_DIR":/cfg:ro \
        -e HOME=/w/home -e ORT_DATA_DIR=/w/ortdata -e JAVA_TOOL_OPTIONS="-Djava.io.tmpdir=/w/ortdata/tmp ${ORT_JAVA_OPTS:-}" ${ORT_DOCKER_OPTS:-} \
        "$ORT_IMAGE" "$@"
}

stage_spdx() {
    mkdir -p "$ORT_WORK/in"
    python3 "$HERE/make_image_spdx.py" "${SPDX_ROOT:?}" "${IMAGE_MANIFEST:?}" "$ORT_WORK/in/image.spdx.json"
}
stage_analyze() {
    ort -P ort.analyzer.enabledPackageManagers=SpdxDocumentFile analyze -i /w/in -o /w/analyze
}
stage_scan() {  # resumable: the results are kept in $ORT_WORK/ortdata
    mkdir -p "$ORT_WORK/scan"
    ort ${ORT_LOG_OPT:-} scan -i /w/analyze/analyzer-result.yml -o /w/scan -s ScanCode || echo "scan exit=$? (failed downloads are expected)"
    [ -f "$ORT_WORK/scan/scan-result.yml" ] || { echo "no scan result was made" >&2; return 1; }
}
stage_pkgconf() {
    python3 "$HERE/gen_pkgconf.py" "$ORT_WORK/scan/scan-result.yml" "$ORT_WORK/pkgconf"
}
stage_evaluate() {
    ort evaluate -i /w/scan/scan-result.yml -o /w/evaluate \
        --rules-file /cfg/evaluator.rules.kts \
        --license-classifications-file /cfg/license-classifications.yml \
        --resolutions-file /cfg/resolutions.yml \
        --package-curations-file /cfg/sparrow-hawk/curations.yml \
        --package-configurations-dir /w/pkgconf
}
stage_report() {
    ort report -i /w/evaluate/evaluation-result.yml -o /w/report -f StaticHtml,WebApp,CycloneDx,PlainTextTemplate \
        --license-classifications-file /cfg/license-classifications.yml \
        --resolutions-file /cfg/resolutions.yml \
        --custom-license-texts-dir /cfg/custom-license-texts
}

stages=("$@")
[ "${stages[0]:-}" = all ] && stages=(spdx analyze scan pkgconf evaluate report)
[ ${#stages[@]} -eq 0 ] && { sed -n 2,5p "$0"; exit 1; }
for s in "${stages[@]}"; do echo "=== $s ($(date -u +%T))"; "stage_$s"; done
