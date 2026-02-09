#!/bin/sh
#
# Copyright (c) 2025-2026, Renesas Electronics Corporation. All rights reserved.
# SPDX-License-Identifier: MIT
#
SCRIPT_DIR=$(cd `dirname $0` && pwd)

cd ${SCRIPT_DIR}
pip3 install pyserial colorama tqdm
python3 burn.py

