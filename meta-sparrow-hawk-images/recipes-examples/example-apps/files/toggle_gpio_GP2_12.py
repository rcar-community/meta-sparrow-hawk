#!/usr/bin/env python3
#
# This program is written according to following document:
#     https://libgpiod.readthedocs.io/en/latest/python_api.html
#

import time
import gpiod
from gpiod.line import Value, Direction
OUTPUT   = Direction.OUTPUT
INPUT    = Direction.INPUT
ACTIVE   = Value.ACTIVE
INACTIVE = Value.INACTIVE

# GP2_12
GPIO_CHIP=2
GPIO_LINE=12

GPIO_CHIP_DEV=f"/dev/gpiochip{GPIO_CHIP}"
GPIO_NAME=f"GP{GPIO_CHIP}_{GPIO_LINE}"

with gpiod.Chip(path=GPIO_CHIP_DEV) as chip:
    line = chip.request_lines(
            config={
                GPIO_LINE: gpiod.LineSettings(direction=OUTPUT)
                }
            )
    print(f"Toggle {GPIO_NAME} every 1sec")
    print(f"  If you want to exit this application, please use CTRL+C\n")
    while True:
        line.set_value(GPIO_LINE, ACTIVE)
        print(f"\rCurrent Status is ACTIVE  ", end="")
        time.sleep(1)
        line.set_value(GPIO_LINE, INACTIVE)
        print(f"\rCurrent Status is INACTIVE", end="")
        time.sleep(1)

