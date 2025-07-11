#!/bin/sh

SCRIPT_DIR=$(cd `dirname $0` && pwd)

cd ${SCRIPT_DIR}
pip3 install pyserial colorama tqdm
python3 burn.py

