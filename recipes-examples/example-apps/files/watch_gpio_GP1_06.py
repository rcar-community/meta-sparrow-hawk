#!/usr/bin/env python3
#
# This program is written according to following document:
#     https://libgpiod.readthedocs.io/en/latest/python_api.html
#
# Wiring (CN7 40-pin header):
#     Pin 7 (GPIO 4 / GP1_06) --- push button --- Pin 9 (GND)
#
# No external pull-up resistor is needed. GPIOs of CN7 are connected through
# the level shifter (TXS0108E) on the board, which has an internal pull-up, so
# the line is HIGH while the button is released and LOW while it is pressed.
# Do not add an external pull-down resistor.
#

import sys
from datetime import timedelta
import gpiod
from gpiod.line import Value, Direction, Edge
INPUT     = Direction.INPUT
ACTIVE    = Value.ACTIVE
BOTH      = Edge.BOTH
RISING    = gpiod.EdgeEvent.Type.RISING_EDGE
FALLING   = gpiod.EdgeEvent.Type.FALLING_EDGE

# GP1_06
GPIO_CHIP=1
GPIO_LINE=6

GPIO_CHIP_DEV=f"/dev/gpiochip{GPIO_CHIP}"
GPIO_NAME=f"GP{GPIO_CHIP}_{GPIO_LINE:02d}"

# Ignore the chattering of the button
DEBOUNCE_PERIOD=timedelta(milliseconds=10)

with gpiod.Chip(path=GPIO_CHIP_DEV) as chip:
    with chip.request_lines(
            consumer="watch_gpio",
            config={
                GPIO_LINE: gpiod.LineSettings(
                    direction=INPUT,
                    edge_detection=BOTH,
                    debounce_period=DEBOUNCE_PERIOD)
                }
            ) as line:
        print(f"Watch {GPIO_NAME} (Pin 7 of CN7)")
        print(f"  If you want to exit this application, please use CTRL+C\n")

        # Current status
        value = line.get_value(GPIO_LINE)
        print(f"Current Status is {'HIGH (released)' if value == ACTIVE else 'LOW (pressed)'}")

        try:
            while True:
                # Wait for the edge event without busy loop
                if not line.wait_edge_events():
                    continue
                for event in line.read_edge_events():
                    if event.event_type == FALLING:
                        print(f"[{event.timestamp_ns / 1e9:.3f}] FALLING edge: LOW (pressed)")
                    elif event.event_type == RISING:
                        print(f"[{event.timestamp_ns / 1e9:.3f}] RISING edge:  HIGH (released)")
        except KeyboardInterrupt:
            print("\nExit")
            sys.exit(0)
