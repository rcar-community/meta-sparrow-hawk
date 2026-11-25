#!/usr/bin/env python3
"""Review of the NOASSERTION-only findings of the ORT source scan.

usage: na_review.py find | collect | classify | check
  find      list the files whose findings are all NOASSERTION (after the path excludes)  -> $ORT_WORK/na/na_only_paths.json
  collect   get the text of every finding (tarballs and the git revision of the scan)   -> $ORT_WORK/na/na_texts.json
  classify  classify the texts by regular expressions                                  -> $ORT_WORK/na/na_review.yml
            (copy it to scripts/ort/na_review.yml after reading it)
  check     list the NOASSERTION-only files that na_review.yml does not cover (exit 1 if any)

Categories: A=reference to another license file, B=not a license (false positive),
            C=own permissive wording, D=SPDX notation variant.
"""
import collections, glob, io, json, os, re, sys, tarfile, time, urllib.error, urllib.request
import yaml
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import load_yaml, NA_DIR, NA_REVIEW, SCAN_RESULT, PKGCONF, ARCHIVES

P = lambda n: os.path.join(NA_DIR, n)
os.makedirs(NA_DIR, exist_ok=True)


def rx(p):
    r = re.escape(p).replace(r'\*\*/', '(?:.*/)?').replace(r'\*\*', '.*').replace(r'\*', '[^/]*').replace(r'\?', '[^/]')
    return re.compile('^' + r + '$')


def find():
    d = load_yaml(SCAN_RESULT)
    ex = {}
    for f in glob.glob(os.path.join(PKGCONF, '*.yml')):
        c = yaml.safe_load(open(f))
        u = c.get('source_artifact_url') or c.get('vcs', {}).get('url')
        ex.setdefault(u, []).extend(x['pattern'] for x in c.get('path_excludes', []))
    cache, files, revs = {}, collections.defaultdict(lambda: collections.defaultdict(list)), {}
    for e in d['scanner']['provenances']:
        pp = e.get('package_provenance') or {}
        v = pp.get('vcs_info') or pp.get('vcs')
        if v and pp.get('resolved_revision'):
            revs[v['url']] = pp['resolved_revision']
    for r in d['scanner']['scan_results']:
        p = r['provenance']
        url = (p.get('source_artifact') or {}).get('url') or (p.get('vcs_info') or {}).get('url')
        if url not in cache:
            cache[url] = [rx(x) for x in ex.get(url, [])]
        for l in r.get('summary', {}).get('licenses', []):
            path = l['location']['path']
            if any(x.match(path) for x in cache[url]):
                continue
            files[url][path].append([l.get('license', ''), l['location'].get('start_line'), l['location'].get('end_line')])
    out = {}
    for u, fs in files.items():
        na = {p: v for p, v in fs.items() if {x[0] for x in v} == {'NOASSERTION'}}
        if na:
            out[u] = na
    json.dump(out, open(P('na_only_paths.json'), 'w'))
    json.dump(revs, open(P('revs.json'), 'w'))
    return out


# raw file URL of the git sources: <repo name> -> template (revision, path)
RAW_GIT = {
    'u-boot.git': 'https://raw.githubusercontent.com/u-boot/u-boot/%s/%s',
    'opencv.git': 'https://raw.githubusercontent.com/opencv/opencv/%s/%s',
    'e2fsprogs.git': 'https://raw.githubusercontent.com/tytso/e2fsprogs/%s/%s',
    'glibc.git': 'https://raw.githubusercontent.com/bminor/glibc/%s/%s',
    'libnvme.git': 'https://raw.githubusercontent.com/linux-nvme/libnvme/%s/%s',
    'nvme-cli.git': 'https://raw.githubusercontent.com/linux-nvme/nvme-cli/%s/%s',
    'abseil-cpp.git': 'https://raw.githubusercontent.com/abseil/abseil-cpp/%s/%s',
    'protobuf.git': 'https://raw.githubusercontent.com/protocolbuffers/protobuf/%s/%s',
    'systemd-stable.git': 'https://raw.githubusercontent.com/systemd/systemd-stable/%s/%s',
    'binutils-gdb.git': 'https://raw.githubusercontent.com/bminor/binutils-gdb/%s/%s',
    'ncurses-snapshots.git': 'https://raw.githubusercontent.com/ThomasDickey/ncurses-snapshots/%s/%s',
    'init-system-helpers.git': 'https://salsa.debian.org/debian/init-system-helpers/-/raw/%s/%s',
    'linux.git': 'https://raw.githubusercontent.com/rcar-community/linux/%s/%s',
}
# big tarballs are read from the tag of the upstream git repository instead (name -> template (path))
RAW_TAG = {
    'linux-6.6.tar.xz': 'https://raw.githubusercontent.com/torvalds/linux/v6.6/%s',
    'gcc-13.5.0.tar.xz': 'https://raw.githubusercontent.com/gcc-mirror/gcc/releases/gcc-13.5.0/%s',
}


def get(url, tries=4):
    for i in range(tries):
        try:
            return urllib.request.urlopen(url, timeout=60).read()
        except urllib.error.HTTPError:
            raise  # 404 etc. do not get better
        except Exception:
            if i == tries - 1:
                raise
            time.sleep(5 * (i + 1))


def unpack(url, dest):
    if os.path.isdir(dest):
        return
    data = get(re.sub(r'(?<!:)//+', '/', url))  # download first: a failure must not leave an empty directory
    with tarfile.open(fileobj=io.BytesIO(data)) as t:
        t.extractall(dest, filter='data')


def collect(na):
    revs = json.load(open(P('revs.json')))
    out = {}
    for url, fs in na.items():
        name = url.split('/')[-1]
        for path, v in fs.items():
            s, e = v[0][1], v[0][2]
            txt = None
            try:
                if url.endswith(ARCHIVES):
                    if name in RAW_TAG:
                        data = get(RAW_TAG[name] % path.split('/', 1)[1])
                    else:
                        unpack(url, os.path.join(NA_DIR, 'src', name))
                        data = open(os.path.join(NA_DIR, 'src', name, path), 'rb').read()
                else:
                    data = get(RAW_GIT[name] % (revs[url], path))
                txt = ' '.join(data.decode(errors='replace').split('\n')[s - 1:e])
            except Exception as ex:
                print('no text:', name, path, str(ex)[:40], file=sys.stderr)
            out.setdefault(url, {})[path] = {'text': txt, 'lines': [s, e]}
    json.dump(out, open(P('na_texts.json'), 'w'))
    print(sum(len(v) for v in out.values()), 'findings,', sum(1 for fs in out.values() for v in fs.values() if v['text'] is None), 'without text')


# text of the files that cannot be fetched (checked by hand)
INFER = {('u-boot.git', 'board/adi/sc573-ezlite/sc573-ezlite.env'): 'SPDX-License-Identifier: GPL-2.0-or-later+',
         ('protobuf.git', 'third_party/utf8_range/CONTRIBUTING.md'): 'Contributor License Agreement',
         ('binutils-gdb.git', 'zlib/contrib/pascal/zlibpas.pas'): 'For conditions of distribution and use, see copyright notice in readme.txt'}
# (category, ORT reason, concluded license, regex on the normalized lower-case text); the first match wins
# D is made of notation variants of a known license; C is not curated (stays NOASSERTION, recorded as reviewed)
RULES = [
 ('D', 'INCORRECT', 'GPL-2.0-or-later', r'spdx-license-identifier: gpl-2\.0\+'),
 ('A', 'REFERENCE', 'NONE', r'license found in (the )?file named copying|legalcopyright", "see licen[cs]e'),
 ('B', 'INCORRECT', 'NONE', r'show warranty|echo "this is free software'),
 ('C', None, None, r'without any expressed or implied warranty'),
 ('D', 'INCORRECT', 'GPL-2.0-or-later', r'spdx-license-identifier: gpl-2\.0-or-later\+'),
 ('D', 'INCORRECT', 'LicenseRef-scancode-public-domain', r'licenseref-lookup3-public-domain'),
 ('D', 'INCORRECT', None, r'spdx-license-identifier-bsd'),
 ('D', 'INCORRECT', None, r'licensed under bsd-mit'),
 ('B', 'INCORRECT', 'NONE', r'contributor license agreement|license agreement|software license agreement|cpack_resource_file_license|license file="license\.rtf"'),
 ('B', 'INCORRECT', 'NONE', r'self\.license = none|"license_file", "license_files"|\$\(install_data\) .*license|legalcopyright", "see licence'),
 ('B', 'INCORRECT', 'NONE', r'used freely without restrictions|^.{0,40}the ncurses package also has|boston, ma|mass ave|^.{0,10}for filesystems created|^some platforms, this function'),
 ('A', 'REFERENCE', 'NONE', r'see copyright for the status|see (the )?(copying|copyright|licen[cs]e)|see \.\./copying|for licen[cs]ing details|see copyright notice|see licen[cs]e file|license: see|see the accompanying file|see licence for|see copying|see license for|see license file|see copyright notice in|licen[cs]e file for details|same licence than quilt|see licen[cs]e for copying|see licen[cs]e\.|see licence'),
 ('C', None, None, r'permission to use|permission is granted|unlimited permission|provided "as is"|no warranty|absolutely no warranty|distributed under the same terms|this is free software|free to use|may be modified, used, copied|at your own risk|redistribution and modification in whole|rights reserved\. no warranty|cnri|different from the notice'),
 ('C', None, None, r'special exception, when this file is copied by dh-make|use, modify, distribute this table freely'),
 ('B', 'INCORRECT', 'NONE', r'warranty|bsd-like|license'),
]


def classify():
    t = json.load(open(P('na_texts.json')))
    out, bad = {}, []
    for url, fs in t.items():
        name = url.split('/')[-1]
        ents = []
        for path, v in sorted(fs.items()):
            tx = re.sub(r'\s+', ' ', (v['text'] or INFER.get((name, path), '')).strip())
            low = re.sub(r'\s+', ' ', re.sub(r'(?:(?<=\s)|^)(?:\*+|//+|#+|;+)(?=\s)', ' ', tx.lower()))
            for c, reason, con, rg in RULES:
                if re.search(rg, low):
                    if 'licensed under bsd-mit' in low:
                        con = 'MIT'
                    rel = path.split('/', 1)[1] if url.endswith(ARCHIVES) else path
                    ents.append({'path': '**/' + rel, 'category': c, 'reason': reason, 'concluded_license': con,
                                 'text': tx[:110], 'inferred': v['text'] is None})
                    break
            else:
                bad.append((name, path, tx[:80]))
        out[url] = ents
    for b in bad:
        print('UNCLASSIFIED', *b)
    if bad:
        sys.exit(1)
    yaml.safe_dump(out, open(P('na_review.yml'), 'w'), sort_keys=True, allow_unicode=True, width=200)
    print(sum(len(v) for v in out.values()), 'entries ->', P('na_review.yml'),
          dict(collections.Counter(e['category'] for v in out.values() for e in v)))


def check(na):
    rv = yaml.safe_load(open(NA_REVIEW)) or {}
    un = []
    for url, fs in na.items():
        rel = {e['path'][3:] for e in rv.get(url, [])}
        for p in fs:
            if (p.split('/', 1)[1] if url.endswith(ARCHIVES) else p) not in rel:
                un.append((url.split('/')[-1], p))
    print('unreviewed NOASSERTION-only files:', len(un))
    for u in un:
        print(' ', *u)
    sys.exit(1 if un else 0)


if __name__ == '__main__':
    cmd = sys.argv[1] if len(sys.argv) > 1 else ''
    if cmd == 'find':
        print('NOASSERTION-only files:', sum(len(v) for v in find().values()))
    elif cmd == 'collect':
        collect(find())
    elif cmd == 'classify':
        classify()
    elif cmd == 'check':
        check(find())
    else:
        sys.exit(__doc__)
