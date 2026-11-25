import json, re, sys, os, glob
spdx_root, manifest, out = sys.argv[1:4]
found, missing, vermis = [], [], []
# recipe name -> primary source URL (first http(s)/git source in the recipe SPDX)
_rc = {}
def primary_source(pkgdoc):
    m = None
    for r in pkgdoc.get('relationships', []):
        if r.get('relationshipType') == 'GENERATED_FROM':
            m = re.match(r'DocumentRef-recipe-(.+?):', r['relatedSpdxElement'])
            if m: break
    if not m: return 'NOASSERTION'
    rn = m.group(1)
    rn = {'libgcc': 'gcc', 'gcc-runtime': 'gcc', 'libstdc++': 'gcc'}.get(rn, rn)
    if rn not in _rc:
        _rc[rn] = 'NOASSERTION'
        # the recipe itself, then recipes sharing the source (glibc-locale -> glibc, gcc -> gcc-source-*)
        cands = [f'recipe-{rn}.spdx.json', f'recipe-{rn.rsplit("-", 1)[0]}.spdx.json', f'recipe-{rn}-source-*.spdx.json', f'recipe-{rn.split("-")[0]}-source-*.spdx.json']
        for c in cands:
            for fn in sorted(glob.glob(f'{spdx_root}/*/recipes/{c}')):
                for p in json.load(open(fn))['packages']:
                    u = p.get('downloadLocation', '')
                    if '-source-' not in p['name']: continue
                    u = re.sub(r'^gitsm\+', 'git+', u)
                    if re.match(r'(https?|git\+https?|git\+git)://', u):
                        _rc[rn] = u; break
                if _rc[rn] != 'NOASSERTION': break
            if _rc[rn] != 'NOASSERTION': break
    return _rc[rn]
lic_ids = set()
pk = []
for line in open(manifest):
    name, arch, ver = line.split()
    d = None
    for a in (arch, 'cortexa76', 'sparrow_hawk', 'allarch'):
        f = f'{spdx_root}/{a}/packages/{name}.spdx.json'
        if os.path.exists(f):
            d = json.load(open(f)); break
    if not d:
        missing.append(name); continue
    p = d['packages'][0]
    lic = p.get('licenseDeclared', 'NOASSERTION')
    lic = re.sub(r'DocumentRef-[^:\s]+:', '', lic)
    lic_ids.update(re.findall(r'LicenseRef-[A-Za-z0-9.\-]+', lic))
    if p.get('versionInfo') and p['versionInfo'] not in ver:
        vermis.append((name, ver, p['versionInfo']))
    pk.append({'SPDXID': 'SPDXRef-' + re.sub(r'[^A-Za-z0-9.\-]', '-', name),
               'name': name, 'versionInfo': p.get('versionInfo', ver.split('-r')[0]),
               'supplier': p.get('supplier', 'NOASSERTION'),
               'downloadLocation': primary_source(d), 'filesAnalyzed': False,
               'licenseConcluded': 'NOASSERTION', 'licenseDeclared': lic,
               'copyrightText': 'NOASSERTION'})
doc = {'spdxVersion': 'SPDX-2.2', 'dataLicense': 'CC0-1.0', 'SPDXID': 'SPDXRef-DOCUMENT',
       'name': 'core-image-weston-sparrow-hawk', 'documentNamespace': 'http://example.invalid/sparrow-hawk-weston',
       'creationInfo': {'created': '2026-11-06T00:00:00Z', 'creators': ['Tool: make_image_spdx.py']},
       'packages': pk,
       'hasExtractedLicensingInfos': [{'licenseId': i, 'extractedText': 'see Yocto recipe', 'name': i} for i in sorted(lic_ids)],
       'relationships': [{'spdxElementId': 'SPDXRef-DOCUMENT', 'relationshipType': 'DESCRIBES', 'relatedSpdxElement': 'SPDXRef-DOCUMENT'}]}
root = {'SPDXID': 'SPDXRef-Image', 'name': 'core-image-weston', 'versionInfo': '1.0', 'downloadLocation': 'NOASSERTION', 'filesAnalyzed': False, 'licenseConcluded': 'NOASSERTION', 'licenseDeclared': 'MIT', 'copyrightText': 'NOASSERTION'}
doc['packages'].insert(0, root)
doc['relationships'] = [{'spdxElementId': 'SPDXRef-DOCUMENT', 'relationshipType': 'DESCRIBES', 'relatedSpdxElement': 'SPDXRef-Image'}] + [{'spdxElementId': 'SPDXRef-Image', 'relationshipType': 'DEPENDS_ON', 'relatedSpdxElement': p['SPDXID']} for p in pk]
json.dump(doc, open(out, 'w'), indent=1)
print('manifest pkgs', len(pk) + len(missing), 'found', len(pk), 'missing', len(missing), 'version-mismatch', len(vermis))
print('missing sample', missing[:15]); print('vermis sample', vermis[:5])
