#!/bin/bash

SCRIPT_DIR=$(cd `dirname $0` && pwd)
cd ${SCRIPT_DIR}/../

#python3 -m venv --clear .env
install_oelint_adv () {
    python3 -m venv .venv
    . .venv/bin/activate
    pip3 install git+https://github.com/priv-kweihmann/oelint-adv --quiet
}
install_oelint_adv

# oelint-adv --print-rulefile > ./oelint-rules.json

SEARCH_DIRS="${SCRIPT_DIR}/../recipe* ${SCRIPT_DIR}/../conf ${SCRIPT_DIR}/../include ${SCRIPT_DIR}/../wic"
oelint-adv \
    --quiet \
    --color \
    $(find ${SEARCH_DIRS} -name \*.bb -o -name \*.bbappend -o -name \*.inc -o -name \*.conf) \
    $*

