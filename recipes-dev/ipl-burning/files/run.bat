@echo off
REM
REM Copyright (c) 2025-2026, Renesas Electronics Corporation. All rights reserved.
REM SPDX-License-Identifier: MIT
REM

set PATH=%PATH%;%~dp0\python-embed-amd64\Scripts

cd /d %~dp0
cd python-embed-amd64
if not exist Scripts\pip.exe (
    python.exe get-pip.py > nul
)
if not exist Lib\site-packages\serial (
    python.exe -m pip install pyserial colorama tqdm > nul
)
REM clear console output
cls

cd /d %~dp0
python-embed-amd64\python.exe burn.py

