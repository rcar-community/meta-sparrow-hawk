"""Shared helpers of the ORT scripts. All paths are under the work directory ($ORT_WORK, default ./ort-work)."""
import gzip, os, yaml

WORK = os.path.abspath(os.environ.get('ORT_WORK', 'ort-work'))
HERE = os.path.dirname(os.path.abspath(__file__))
NA_REVIEW = os.environ.get('NA_REVIEW', os.path.join(HERE, 'na_review.yml'))
NA_DIR = os.path.join(WORK, 'na')            # intermediate files of the NOASSERTION review
SCAN_RESULT = os.environ.get('SCAN_RESULT', os.path.join(WORK, 'scan', 'scan-result.yml'))
PKGCONF = os.path.join(WORK, 'pkgconf')      # generated package configurations
ARCHIVES = ('.tar.xz', '.tar.gz', '.tar.bz2')


def load_yaml(path):
    op = gzip.open if path.endswith('.gz') else open
    with op(path) as f:
        return yaml.load(f, Loader=yaml.CSafeLoader)
