#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# import section
import os
import sys
import shutil
import subprocess
import serial.tools.list_ports # pyserial
import colorama
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
import ipl_burning

# Global variables
WORK_DIR = os.path.dirname(os.path.abspath(__file__))
IPL_PATH=f"{WORK_DIR}"
MOT_PATH=f"{WORK_DIR}"

# For serial port detection
TARGET_MESSAGE = "please send !"
TIMEOUT_SECONDS = 30 # Port scanning timeout
BAUDRATE = 921600

# Functions

def print_err(str):
    colorama.init()
    print(f'{colorama.Fore.RED}{str}{colorama.Style.RESET_ALL}')

def Usage():
    print(f"Usage:")
    print(f"    {sys.argv[0]} board <comport or serial_device>")
    print(f"comport or serial_device:")
    for comport in serial.tools.list_ports.comports():
        print(f"    {comport}")

import serial
import threading
import time
def monitor_port(port_info, result):
    port = port_info.device
    try:
        with serial.Serial(port, baudrate=BAUDRATE, timeout=1) as ser:
            start_time = time.time()
            while (result["port"] is None) and (time.time() - start_time < TIMEOUT_SECONDS):
                if ser.in_waiting:
                    line = ser.readline().decode(errors='ignore').strip()
                    # print(f"DEBUG: [{port}] {line}")
                    if TARGET_MESSAGE in line:
                        result["port"] = port
                        return
    except:
        # print(f"[{port}] DEBUG: Thread is closed by Error")
        pass
    # print("DEBUG: Thread is closed by Timeout")

def detect_serial_device():
    _ports = list(serial.tools.list_ports.comports())
    ports = [port for port in _ports if not os.path.islink(port.device)] # list up except symbolic link
    if len(ports) == 0:
        print("No serial ports found.")
        return None

    print(f"Scanning {len(ports)} serial ports for message: '{TARGET_MESSAGE}'")
    threads = []
    result = {"port": None}

    for port_info in ports:
        t = threading.Thread(target=monitor_port, args=(port_info, result))
        t.start()
        threads.append(t)

    for t in threads:
        t.join()
        if result["port"] is not None:
            return result["port"]

def print_chapter(msg):
    msg_len = len(msg)
    print( "****************************************") # 40 char
    print(f"* {msg}",end="")
    for i in range(40-msg_len-4):
        print(" ", end="")
    print(" *")
    print( "****************************************");print("")


def print_dip_sw(status=0x55):
    sw = [0]*8
    print("")
    # ascii art
    print("  |1|2|3|4|5|6|7|8|")
    print("  |-|-|-|-|-|-|-|-|")
    for i in range(8): sw[7-i] = "x" if (status>>i) & 0x01 else " "
    print(f"  |{sw[0]}|{sw[1]}|{sw[2]}|{sw[3]}|{sw[4]}|{sw[5]}|{sw[6]}|{sw[7]}| ON")
    for i in range(8): sw[7-i] = " " if (status>>i) & 0x01 else "x"
    print(f"  |{sw[0]}|{sw[1]}|{sw[2]}|{sw[3]}|{sw[4]}|{sw[5]}|{sw[6]}|{sw[7]}| OFF")
    print("")
    # table
    for i in range(8): sw[7-i] = "ON " if (status>>i) & 0x01 else "OFF"
    print( "  | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |")
    print( "  |---|---|---|---|---|---|---|---|")
    print(f"  |{sw[0]}|{sw[1]}|{sw[2]}|{sw[3]}|{sw[4]}|{sw[5]}|{sw[6]}|{sw[7]}|")
    print("")

def sparrow_hawk_instruction_setup():
    print_chapter("ipl burting tool for sparrow-hawk")
    print_chapter("1. Board setup for flashing firmware")
    print("Power off the board, then please set DIP-SW(SW2) as following")
    print_dip_sw(status=0x0f) # 1~4: OFF, 5~8: ON
    print("Then, connect board to PC using USB cable")
    print("  If serial port has already been opened by other application(*),")
    print("  please close the application to use serial port by this script")
    print("  *) Teraterm, picocom, and so on.")
    print("")
    print("Press Enter key to proceed: "); input()
    print("")
    print_chapter("2. Start flashing firmware")
    print("Please power on the board"); print("")

def sparrow_hawk_instruction_tidyup():
    print_chapter("3. Setup board for booting OS image")
    print("Power off the board, then please set DIP-SW(SW2) as following")
    print_dip_sw(status=0xef) # 1~3: ON: 4: OFF, 5~8: ON
    print("Now, you can use bootloader on your board to boot OS image")
    print("")
    print("Close this window(script) or press Enter key to close program: "); input()


# main function
def main():
    BOARD = "sparrow-hawk"
    COM_PORT = "/dev/ttyUSBXX or COMXX"
    BURN_MODE = "all"
    INSTRUCTION_MODE = False

    args = sys.argv
    if "-h" in args:
        Usage(); quit()

    # Instruction mode
    if len(args) <= 1:
        INSTRUCTION_MODE = True
        sparrow_hawk_instruction_setup()
        COM_PORT=detect_serial_device()        
        if COM_PORT is None:
            print_err(f"ERROR: Target board is not found.")
            print("")
            print("Close this window or press Enter key: "); input()
            quit()
    # Automatic mode
    elif args[1] not in [comport.device for comport in serial.tools.list_ports.comports()]:
        print_err(f"ERROR: Please \"input\" correct comport:")
        Usage(); quit()
    else:
        COM_PORT=args[1]

    args = ["ipl_burning.py" ,BOARD, COM_PORT, MOT_PATH, IPL_PATH, BURN_MODE]
    ipl_burning.main(args)

    if INSTRUCTION_MODE is True:
        sparrow_hawk_instruction_tidyup()

if __name__ == "__main__":
    main()

