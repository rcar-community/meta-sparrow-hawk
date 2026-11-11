#!/bin/bash
# Generate the release notes of the Sparrow Hawk Yocto BSP (GitHub release body).
#
# Usage: gen_release_notes.sh <output file>
#
# Environment variables:
#   TAG_NAME           Release tag (ex. v2026-10-30, dev-latest, next-release)
#   BRANCH             Branch name (default: current branch)
#   BUILD_DIR          Directory where build.sh built the image (default: ./build)
#   GITHUB_REPOSITORY  owner/repo, used to read the previous release (optional)
#   GITHUB_TOKEN       Token for gh (optional)
#   PREV_MANIFEST_DIR  Directory with the manifests of the previous release.
#                      If not set, they are downloaded from the previous release.
#
# Run it from the root of meta-sparrow-hawk after the build.

set -u

OUTPUT="${1:?Usage: $0 <output file>}"
BRANCH="${BRANCH:-$(git rev-parse --abbrev-ref HEAD)}"
TAG_NAME="${TAG_NAME:-}"
BUILD_DIR="${BUILD_DIR:-./build}"
DEPLOY_DIR="${BUILD_DIR}/build-sparrow-hawk/tmp/deploy/images/sparrow-hawk"
KNOWN_ISSUES_FILE=".github/release-notes/known-issues.md"
IMAGES="weston minimal"

# ---------------------------------------------------------------- helpers
# Print the path of the manifest of an image (empty if not found)
manifest_path() {
    local f="${DEPLOY_DIR}/core-image-$1-sparrow-hawk.rootfs.manifest"
    [[ -e "${f}" ]] || f=$(ls "${DEPLOY_DIR}"/core-image-"$1"-sparrow-hawk.rootfs-*.manifest 2>/dev/null | tail -1)
    [[ -n "${f}" && -e "${f}" ]] && echo "${f}"
}

# Compare two manifests ("<package> <arch> <version>" per line).
# Print the changed, added and removed packages as collapsible tables.
diff_manifests() {
    local old="$1" new="$2"
    awk '
        FNR == NR { old[$1] = $3; next }
        { new[$1] = $3 }
        END {
            for (p in new) {
                if (!(p in old))          add[p] = new[p]
                else if (old[p] != new[p]) chg[p] = old[p] " | " new[p]
            }
            for (p in old) if (!(p in new)) del[p] = old[p]
            nc = 0; na = 0; nd = 0
            for (p in chg) nc++
            for (p in add) na++
            for (p in del) nd++
            printf "<details><summary>Updated packages (%d)</summary>\n\n", nc
            if (nc) { print "| Package | Previous | Current |\n| --- | --- | --- |" }
            for (p in chg) print "| " p " | " chg[p] " |" | "sort"
            close("sort")
            printf "\n</details>\n\n<details><summary>Added packages (%d)</summary>\n\n", na
            if (na) { print "| Package | Version |\n| --- | --- |" }
            for (p in add) print "| " p " | " add[p] " |" | "sort"
            close("sort")
            printf "\n</details>\n\n<details><summary>Removed packages (%d)</summary>\n\n", nd
            if (nd) { print "| Package | Version |\n| --- | --- |" }
            for (p in del) print "| " p " | " del[p] " |" | "sort"
            close("sort")
            print "\n</details>"
        }' "${old}" "${new}"
}

# Print the value of the 2nd column of a "| name | value | ..." table row
# in the previous release body ("-" if not found)
previous_value() {
    local name="$1"
    [[ -n "${PREV_BODY:-}" ]] || { echo "-"; return; }
    printf '%s\n' "${PREV_BODY}" | awk -F'|' -v n="${name}" '
        { k=$2; gsub(/^ +| +$/, "", k) }
        k == n { v=$3; gsub(/^ +| +$/, "", v); print v; found=1; exit }
        END { if (!found) print "-" }'
}

# ---------------------------------------------------------------- previous release
CUR_TAG="${TAG_NAME}"
PREV_TAG=$(git tag --merged HEAD --list 'v[0-9]*' --sort=-v:refname 2>/dev/null |
    grep -vx "${CUR_TAG}" | head -1 || true)
PREV_BODY=""
if [[ -n "${PREV_TAG}" && -n "${GITHUB_REPOSITORY:-}" ]] && command -v gh >/dev/null 2>&1; then
    PREV_BODY=$(gh release view "${PREV_TAG}" --repo "${GITHUB_REPOSITORY}" --json body -q .body 2>/dev/null || true)
fi
if [[ -z "${PREV_MANIFEST_DIR:-}" && -n "${PREV_TAG}" && -n "${GITHUB_REPOSITORY:-}" ]] &&
        command -v gh >/dev/null 2>&1; then
    PREV_MANIFEST_DIR=$(mktemp -d)
    gh release download "${PREV_TAG}" --repo "${GITHUB_REPOSITORY}" \
        --pattern 'core-image-*-sparrow-hawk.manifest' --dir "${PREV_MANIFEST_DIR}" >/dev/null 2>&1 || true
fi

# ---------------------------------------------------------------- versions
YOCTO_VER=$(grep ^DISTRO_VERSION "${BUILD_DIR}/poky/meta-poky/conf/distro/poky.conf" | cut -d'"' -f2)
UBOOT_VER=$(grep ^PV ./recipes-bsp/u-boot/u-boot_git.bb | cut -d'"' -f2 | cut -d'+' -f1)
KERNEL_VER=$(ls "${DEPLOY_DIR}"/Image--* 2>/dev/null | head -1 | sed -e 's/.*--//' -e 's/+git.*//')
GCC_VER=$(ls -d "${BUILD_DIR}"/build-sparrow-hawk/tmp/work-shared/gcc-* 2>/dev/null |
    grep -oP 'gcc-\K[0-9]+\.[0-9]+\.[0-9]+' | head -1)
ATF_VER=$(grep ^PV ./recipes-bsp/arm-trusted-firmware/arm-trusted-firmware_git.bb | cut -d'"' -f2 | cut -d'+' -f1)
BSP_VER=$(grep 'BSP_VERSION ??=' recipes-bsp/bsp-info/bsp-info.bb | cut -d'"' -f2 || true)
COMMIT=$(git log -n1 --pretty="format:%H")

# ---------------------------------------------------------------- output
{
    if [[ "${BRANCH}" == *-dev ]]; then
        echo '## Note: This is the latest development build.'
    elif [[ "${BRANCH}" == *-next ]]; then
        echo '## Note: This is a release candidate.'
    else
        echo "## Sparrow Hawk Yocto BSP ${TAG_NAME}"
    fi

    cat << EOS

- Sparrow Hawk (Renesas R-Car V4H) BSP built with Yocto Project ${YOCTO_VER} (scarthgap)
- Images: \`core-image-minimal\` (console) and \`core-image-weston\` (Weston GUI)
- Previous release: ${PREV_TAG:--}

## Software versions

| Software                    | Version       |
| --------------------------- | ------------- |
EOS
    for row in \
        "Yocto Project|${YOCTO_VER}" \
        "Aarch64-poky-linux-gcc(GCC)|${GCC_VER}" \
        "Linux kernel|${KERNEL_VER}" \
        "U-Boot|${UBOOT_VER}" \
        "Trusted Firmware-A|${ATF_VER}" \
        "Userland 64/32bit|64"; do
        name="${row%%|*}"
        ver="${row#*|}"
        prev=$(previous_value "${name}")
        # Show the previous version in parentheses only when it changed
        if [[ "${prev}" != "-" && "${prev}" != "${ver}" ]]; then
            echo "| ${name} | ${ver} (previous: ${prev}) |"
        else
            echo "| ${name} | ${ver} |"
        fi
    done

    echo
    echo "## Package changes since ${PREV_TAG:-the previous release}"
    for img in ${IMAGES}; do
        new_manifest=$(manifest_path "${img}" || true)
        old_manifest="${PREV_MANIFEST_DIR:-}/core-image-${img}-sparrow-hawk.manifest"
        echo
        echo "### core-image-${img}"
        echo
        if [[ -z "${new_manifest}" ]]; then
            echo "The package list of this image was not found."
        elif [[ ! -f "${old_manifest}" ]]; then
            echo "The previous release has no package list. The comparison starts from the next release."
        else
            diff_manifests "${old_manifest}" "${new_manifest}"
        fi
    done

    # Optional: add the file when there are known issues to publish
    if [[ -f "${KNOWN_ISSUES_FILE}" ]]; then
        echo
        echo "## Known issues and limitations"
        echo
        # Skip HTML comments that explain how to edit the file
        sed '/^<!--/,/-->/d' "${KNOWN_ISSUES_FILE}"
    fi

    cat << 'EOS'

## Downloads

Files named `*.zip`, `*.z01`, `*.z02`, ... are one split zip archive (each part is under 2 GB).
Download all the parts into the same directory, then extract it:

```
# 7-Zip: extracts the parts automatically
7z x deploy_sparrow-hawk.zip

# Info-ZIP: join the parts into one zip file first
zip -s 0 deploy_sparrow-hawk.zip --out joined.zip && unzip joined.zip
```

## Yocto layer information

| Layer | Repository | Commit |
| --- | --- | --- |
EOS
    for layer in $(ls "${BUILD_DIR}" | grep -v build-); do
        [[ -d "${BUILD_DIR}/${layer}/.git" || -f "${BUILD_DIR}/${layer}/.git" ]] || continue
        URL=$(git -C "${BUILD_DIR}/${layer}/" remote -v | head -1 | awk '{print $2}')
        C=$(git -C "${BUILD_DIR}/${layer}/" log -n1 --pretty="format:%H")
        echo "| ${layer} | ${URL} | ${C} |"
    done
    URL=$(git -C ./ remote -v | head -1 | awk '{print $2}')
    echo "| meta-sparrow-hawk | ${URL} | ${COMMIT} |"

    cat << 'EOS'

## License and source code

- The full text of the license of each package is included in the SPDX files in `deploy_spdx_split.zip`.
- The source code, the patches and the recipes of the GPL/LGPL/AGPL packages are provided in `deploy_sources_split.zip`. The sources are stored per recipe in `sources/<architecture>/<recipe>-<version>/`.
- The graphics user-space libraries (`gles-user-module`) are provided under the Renesas-GFX license:
  - Redistribution and use in binary form are permitted without modification.
  - No reverse engineering, decompilation, or disassembly is permitted.
  - Any redistribution and use are licensed for use only with Renesas Electronics's devices.
EOS
} > "${OUTPUT}"
