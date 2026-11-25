# ORT license report

Scripts to make the license report of the Sparrow Hawk image with the
[OSS Review Toolkit (ORT)](https://oss-review-toolkit.org/).

The report is made from the SPDX documents of the Yocto build (`build.sh --sbom`).
ORT scans the sources of every package with ScanCode and evaluates the result
with the policy in the ort-config repository.

## Files

| File | Role |
|---|---|
| `run.sh` | Runs the stages below in docker (ORT is pinned to a version) |
| `make_image_spdx.py` | Makes one SPDX document of the image from the Yocto SPDX files and the image manifest |
| `gen_pkgconf.py` | Makes the ORT package configurations: path excludes and the NOASSERTION review |
| `na_review.py` | Reviews the NOASSERTION-only findings (`find`, `collect`, `classify`, `check`) |
| `na_review.yml` | Result of the NOASSERTION review (category and reason of every file) |
| `common.py` | Shared paths |

The policy (license classifications, resolutions, package curations, evaluator rules)
is in the ort-config repository, branch `sparrow-hawk-work`
(`https://github.com/yhamamachi/ort-config`).

## Stages

`scripts/ort/run.sh <stage>...` (or `all`). Everything is made under `$ORT_WORK` (default `./ort-work`).

1. `spdx`: `make_image_spdx.py` with `SPDX_ROOT` (`tmp/deploy/spdx/2.2` of the build) and
   `IMAGE_MANIFEST` (`core-image-weston-sparrow-hawk.manifest`).
2. `analyze`: ORT analyzer (SpdxDocumentFile).
3. `scan`: ORT scanner (ScanCode). **It takes a long time** (about 10 hours on 32 cores for the whole image).
   The results are kept per source in `$ORT_WORK/ortdata`, so that a second run scans only
   what changed. If it is interrupted, run it again and it continues.
4. `pkgconf`: `gen_pkgconf.py` makes `$ORT_WORK/pkgconf` from the scan result.
5. `evaluate`: ORT evaluator with the policy of ort-config.
6. `report`: StaticHtml, WebApp, CycloneDX and NOTICE in `$ORT_WORK/report`.

Without a scan result, the stages 4 to 6 cannot be run. A report without the source
scan only repeats the licenses declared in the recipes and is not useful.

## NOASSERTION review

`NOASSERTION` is the finding of a license text that ScanCode cannot name (for example
"See COPYING"). The files whose findings are all NOASSERTION were read and classified:

- A: reference to another license file (`REFERENCE`, concluded `NONE`)
- B: not a license, false positive (`INCORRECT`, concluded `NONE`)
- C: own permissive wording (not curated, recorded as reviewed)
- D: notation variant of a known license (concluded to the SPDX id)

`na_review.yml` is the result and `gen_pkgconf.py` puts it into the package
configurations as `license_finding_curations`. The curation is matched by the path
and does not read the text, so a new version of a source can add files that are not
reviewed. Find them after a new scan:

```
ORT_WORK=ort-work python3 scripts/ort/na_review.py check     # exit 1 and a list if there is any
ORT_WORK=ort-work python3 scripts/ort/na_review.py collect   # get the text of the findings
ORT_WORK=ort-work python3 scripts/ort/na_review.py classify  # classify by RULES in na_review.py
```

If `classify` cannot classify a text, read it and add a rule to `RULES` in `na_review.py`.
Whether a wording of category C is acceptable is a decision of a person.
Copy `ort-work/na/na_review.yml` to `scripts/ort/na_review.yml` after the review.

## Notes

- The result is the record of ORT and is not a legal opinion.
- The scan result of the image is not in this repository (about 30 MB compressed).
