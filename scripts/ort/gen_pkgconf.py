#!/usr/bin/env python3
# usage: gen_pkgconf.py [scan-result.yml[.gz]] [out dir]    (defaults: $ORT_WORK/scan/scan-result.yml, $ORT_WORK/pkgconf)
# Generate ORT package configurations for every scanned package:
#  - path_excludes: documents, tests, code that is not built into the image (verified)
#  - license_finding_curations: the NOASSERTION review (na_review.yml)
import yaml, sys, os, re
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import load_yaml, SCAN_RESULT, PKGCONF, NA_REVIEW

d = load_yaml(sys.argv[1] if len(sys.argv) > 1 else SCAN_RESULT)
out = sys.argv[2] if len(sys.argv) > 2 else PKGCONF
GENERIC = [
 ("**/doc/**","DOCUMENTATION_OF"),("**/docs/**","DOCUMENTATION_OF"),
 ("**/Documentation/**","DOCUMENTATION_OF"),("**/man/**","DOCUMENTATION_OF"),
 ("**/test/**","TEST_OF"),("**/tests/**","TEST_OF"),("**/testsuite/**","TEST_OF"),
 ("**/testdata/**","TEST_OF"),("**/testing/**","TEST_OF"),
 ("**/po/**","DOCUMENTATION_OF"),("**/fuzzing/**","TEST_OF"),("**/RelNotes/**","DOCUMENTATION_OF"),
 ("**/ChangeLog*","DOCUMENTATION_OF"),("**/CHANGES*","DOCUMENTATION_OF"),("**/README*","DOCUMENTATION_OF"),
 ("**/NEWS*","DOCUMENTATION_OF"),("**/result/**","TEST_OF"),("**/cmake/**","BUILD_TOOL_OF"),("**/Modules/Find*.cmake","BUILD_TOOL_OF"),
 ("**/gendocs_template*","DOCUMENTATION_OF"),("**/tests/files/**","TEST_OF"),
 ("**/manual/**","DOCUMENTATION_OF"),("**/vulkaninfo/vulkaninfo.md","DOCUMENTATION_OF"),
 ("**/*.po","DATA_FILE_OF"),("**/*.pot","DATA_FILE_OF"),
 ("**/Makefile.am","BUILD_TOOL_OF"),("**/Makefile.in","BUILD_TOOL_OF"),("**/config.guess","BUILD_TOOL_OF"),
 ("**/config.sub","BUILD_TOOL_OF"),("**/*.m4","BUILD_TOOL_OF"),("**/configure","BUILD_TOOL_OF"),("**/funding.json","DOCUMENTATION_OF"),
 ("**/examples/**","EXAMPLE_OF"),("**/example/**","EXAMPLE_OF"),("**/samples/**","EXAMPLE_OF"),
]
# Not built into the image (verified: no rndis in Image/modules/u-boot.bin; x86 code not built for arm64)
SPECIFIC = {
 "linux": [("**/drivers/usb/gadget/function/rndis.*","OPTIONAL_COMPONENT_OF"),
           ("**/drivers/media/platform/verisilicon/**","OPTIONAL_COMPONENT_OF"),
           ("**/arch/mips/**","OPTIONAL_COMPONENT_OF"),("**/tools/**","TEST_OF")],
 "gst-plugins-base": [("**/gst-libs/gst/tag/id3v2*.txt","DOCUMENTATION_OF")],
 "taglib": [("**/id3v2/id3v2.*.txt","DOCUMENTATION_OF")],
 "u-boot": [("arch/x86/**","OPTIONAL_COMPONENT_OF"),("drivers/usb/gadget/rndis.*","OPTIONAL_COMPONENT_OF")],
}
SPECIFIC["linux"] += [("**/LICENSES/**","DOCUMENTATION_OF"),("**/scripts/**","BUILD_TOOL_OF"),("**/lib/test_*.c","TEST_OF"),
    ("**/drivers/scsi/qlogicfas*","OPTIONAL_COMPONENT_OF"),("**/drivers/scsi/aha1740.c","OPTIONAL_COMPONENT_OF"),
    ("**/drivers/ata/sata_qstor.c","OPTIONAL_COMPONENT_OF"),("**/drivers/mtd/nand/raw/sharpsl.c","OPTIONAL_COMPONENT_OF")]
SPECIFIC["linux"] += [("**/arch/m68k/**","OPTIONAL_COMPONENT_OF"),("**/arch/microblaze/**","OPTIONAL_COMPONENT_OF"),
    ("**/fs/jffs2/**","OPTIONAL_COMPONENT_OF"),("**/drivers/net/appletalk/**","OPTIONAL_COMPONENT_OF")]
SPECIFIC["linux"] += [("**/drivers/char/xilinx_hwicap/**","OPTIONAL_COMPONENT_OF"),("**/drivers/net/ethernet/broadcom/**","OPTIONAL_COMPONENT_OF"),
    ("**/drivers/net/ethernet/mellanox/**","OPTIONAL_COMPONENT_OF"),("**/drivers/net/ethernet/synopsys/**","OPTIONAL_COMPONENT_OF"),
    ("**/drivers/input/keyboard/tca8418_keypad.c","OPTIONAL_COMPONENT_OF"),("**/drivers/media/dvb-frontends/**","OPTIONAL_COMPONENT_OF"),
    ("**/arch/alpha/**","OPTIONAL_COMPONENT_OF")]
SPECIFIC["linux"] += [("**/drivers/pci/controller/pci-mvebu.c","OPTIONAL_COMPONENT_OF"),("**/drivers/gpu/drm/amd/**","OPTIONAL_COMPONENT_OF")]
SPECIFIC["u-boot"] += [("fs/jffs2/**","OPTIONAL_COMPONENT_OF"),("include/jffs2/**","OPTIONAL_COMPONENT_OF"),("arch/arm/mach-mvebu/**","OPTIONAL_COMPONENT_OF"),("drivers/usb/host/xhci-rcar*","OPTIONAL_COMPONENT_OF"),("Licenses/r8a779x_usb3.txt","OPTIONAL_COMPONENT_OF")]
SPECIFIC["gst-plugins-base"] += [("**/gst-libs/gst/tag/licenses*","DATA_FILE_OF"),("**/gst-libs/gst/tag/license-translations.dict","DATA_FILE_OF"),
    ("**/gst-libs/gst/tag/mklicensestables.c","BUILD_TOOL_OF"),("**/gst-libs/gst/tag/tag.h","OTHER")]
SPECIFIC["protobuf"] = [("java/**","OPTIONAL_COMPONENT_OF"),("ruby/**","OPTIONAL_COMPONENT_OF"),("csharp/**","OPTIONAL_COMPONENT_OF")]
SPECIFIC["opencv"] = [("**/modules/videoio/src/cap_msmf*","OPTIONAL_COMPONENT_OF")]
SPECIFIC["gcc"] = [("libgo/**","OPTIONAL_COMPONENT_OF"),("gcc/go/**","OPTIONAL_COMPONENT_OF")]
# NOASSERTION review (na_review.yml, made by na_review.py): record the reviewed findings as license finding curations
NA = yaml.safe_load(open(NA_REVIEW)) or {}
def na_curations(url):
    return [{'path': e['path'], 'detected_license': 'NOASSERTION', 'concluded_license': e['concluded_license'],
             'reason': e['reason'], 'comment': 'NOASSERTION review (%s): %s' % (e['category'], e['text'][:80])}
            for e in NA.get(url, []) if e['concluded_license']]
n = 0
for e in d['scanner']['provenances']:
    pp = e.get('package_provenance')
    if not pp: continue
    pid = e['id']
    cfg = {'id': pid}
    if pp.get('source_artifact'):
        cfg['source_artifact_url'] = pp['source_artifact']['url']
    elif pp.get('vcs_info') or pp.get('resolved_revision'):
        v = pp.get('vcs_info') or pp.get('vcs')
        cfg['vcs'] = {'type': v['type'], 'url': v['url'], 'revision': pp.get('resolved_revision') or v['revision']}
    else:
        continue
    url = cfg.get('source_artifact_url') or cfg['vcs']['url']
    pats = list(GENERIC)
    if re.search(r'/linux(\.git|-\d[^/]*\.tar)', url): pats += SPECIFIC['linux']
    if 'gst-plugins-base' in url: pats += SPECIFIC['gst-plugins-base']
    if 'taglib' in url: pats += SPECIFIC['taglib']
    if 'gst-plugins-bad' in url: pats += [("**/ext/iqa/**","OPTIONAL_COMPONENT_OF"),("**/sys/decklink/osx/**","OPTIONAL_COMPONENT_OF")]
    if 'numpy' in url: pats += [("**/vendored-meson/**","BUILD_TOOL_OF")]
    if 'flac' in url: pats += [("**/oss-fuzz/**","TEST_OF")]
    if 'pinentry' in url: pats += [("**/tqt/**","OPTIONAL_COMPONENT_OF")]
    if 'gst-plugins-good' in url: pats += [("**/ext/qt/**","OPTIONAL_COMPONENT_OF")]
    if 'Python-3' in url: pats += [("**/PC/**","OPTIONAL_COMPONENT_OF"),("**/PCbuild/**","OPTIONAL_COMPONENT_OF")]
    if 'perl-5' in url: pats += [("**/vms/**","OPTIONAL_COMPONENT_OF"),("**/cpan/CPAN-Meta/**","DOCUMENTATION_OF"),("**/hints/**","OPTIONAL_COMPONENT_OF")]
    if '/protobuf' in url: pats += SPECIFIC['protobuf']
    if '/opencv' in url: pats += SPECIFIC['opencv']
    if 'gcc-13' in url or re.search(r'/gcc[-.]', url): pats += SPECIFIC['gcc']
    if re.search(r'/u-boot(\.git|-)', url): pats += SPECIFIC['u-boot']
    cfg['path_excludes'] = [{'pattern': p, 'reason': r} for p, r in pats]
    if na_curations(url): cfg['license_finding_curations'] = na_curations(url)
    fn = re.sub(r'[^A-Za-z0-9._-]', '_', pid) + '.yml'
    os.makedirs(out, exist_ok=True)
    yaml.safe_dump(cfg, open(os.path.join(out, fn), 'w'), sort_keys=False)
    n += 1
print(n, 'configs')
