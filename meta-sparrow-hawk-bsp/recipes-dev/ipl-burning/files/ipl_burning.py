#!/usr/bin/env python3

#
# Copyright (c) 2023-2026, Renesas Electronics Corporation. All rights reserved.
#
# SPDX-License-Identifier: MIT
#

####################################################################################################
# Import from standard libraries
####################################################################################################
import serial
import sys
import time
import os
import json
from serial.tools.list_ports import comports
from tqdm import tqdm

####################################################################################################
# Print debug data
####################################################################################################
def print_debug(type_mesg, data):
    if sys.platform == "win32":
        if type_mesg == "ERROR":
            PREFIX = "[ERROR]"
        elif type_mesg == "WARNING":
            PREFIX = "[WARNING]"
        elif type_mesg == "INFO":
            PREFIX = "[INFO]"
    else:
        if type_mesg == "ERROR":
            PREFIX = "[\033[01;31mERROR\033[01;0m]"
        elif type_mesg == "WARNING":
            PREFIX = "[\033[01;33mWARNING\033[01;0m]"
        elif type_mesg == "INFO":
            PREFIX = "[\033[01;34mINFO\033[01;0m]"
    print("%s %s" % (PREFIX, data))


class Serial_conn(serial.Serial):
    """
    Serial connection class
    """
    debug_flag = False
    # send a command line
    def sendln(self, cmd_to_send=""):
        str_send = cmd_to_send + "\r"
        return self.write(str_send.encode())

    # send a string
    def send(self, cmd_to_send=""):
        str_send = cmd_to_send
        return self.write(str_send.encode())

    # send a file
    def sendfile(self, file_path):
        print_debug("INFO", "Sending file %s" % file_path)
        return self.write(open(file_path, "rb").read())

    # wait a line by pattern, then send next_command if any, timeout 10 in minutes
    def wait(self, pattern, delay_send=0, cmd_to_send="NONE", time_out=600):
        buffer = ""
        end_time = int(time.time()) + time_out
        while int(time.time()) < end_time:
            bytes_to_read = self.inWaiting()
            if bytes_to_read:
                line = self.read(bytes_to_read).decode('ISO-8859-1')
                buffer += line
                if self.debug_flag is True:
                    sys.stdout.write(line)
                    sys.stdout.flush()
                if pattern in buffer:
                    time.sleep(delay_send)
                    if cmd_to_send != "NONE":
                        self.sendln(cmd_to_send)
                    return 0
        return 255

    # wait for a pattern in list, then send command if any, timeout 10 in minutes
    def waitls(self, patternList=[], delay_send=0, cmd_to_send="NONE", time_out=600):
        buffer = ""
        end_time = int(time.time()) + time_out
        while int(time.time()) < end_time:
            bytes_to_read = self.inWaiting()
            if bytes_to_read:
                line = self.read(bytes_to_read).decode('ISO-8859-1')
                buffer += line
                if self.debug_flag is True:
                    sys.stdout.write(line)
                    sys.stdout.flush()
                index = 0
                for pattern in patternList:
                    if pattern in buffer:
                        time.sleep(delay_send)
                        if cmd_to_send != "NONE":
                            self.sendln(cmd_to_send)
                        return index
                    index = index + 1
        return 255  # timeout


####################################################################################################
# Burn file
####################################################################################################
def flash_burn_file_sparrow_hawk_compat(dev, index, ipl_path, soc, ipl_config, flash_addr):
    dev.debug_flag = True
    dev.sendln()
    dev.wait(">", 0.2, "xls3")

    res = 0
    # Program size
    program_size = os.path.getsize(ipl_path + "/" + ipl_config["ipl"][soc][index]["name"])
    dev.wait("Please Input : H", 0.2, f'{program_size:x}')
    time.sleep(0.4)
    # Program top address
    dev.wait("Please Input : H", 0.2, flash_addr)
    time.sleep(0.4)
    dev.wait("please send ! (binary)", 0.2)
    dev.sendfile(ipl_path + "/" + ipl_config["ipl"][soc][index]["name"])

    ex_list = ["Clear OK?(y/n)", ">"]
    res = dev.waitls(ex_list, 0.1, time_out=10)
    if res == 0:
        dev.send("y")
        time.sleep(0.2)
        dev.sendln()
        dev.sendln()
    dev.debug_flag = False

def flash_burn_file_sparrow_hawk(dev, index, ipl_path, soc, ipl_config, flash_addr):
    dev.sendln()
    dev.wait(">", 0.2)

    # Program size
    program_size = os.path.getsize(ipl_path + "/" + ipl_config["ipl"][soc][index]["name"])
    file_path = ipl_path + "/" + ipl_config["ipl"][soc][index]["name"]
    total_size = os.path.getsize(file_path)

    if total_size > 0x1000:
        progress_flag = True
    else:
        progress_flag = False

    chunk_size = 128 * 1024  # 512 KB
    offset = int(flash_addr,0)
    progress_bar = tqdm(total=total_size, unit='B', unit_scale=True, desc=f"Burning {ipl_config['ipl'][soc][index]['name']}")

    try:
        with open(file_path, "rb") as f:
            while True:
                chunk = f.read(chunk_size)
                if not chunk:
                    break

                dev.sendln()
                dev.wait(">", 0.2, "xls3")

                # チャンクサイズを8桁16進数でASCIIに変換
                size_hex = f"{len(chunk):X}"
                # オフセットを8桁16進数でASCIIに変換
                offset_hex = f"{offset:X}"

                dev.wait("Please Input : H", 0.2, size_hex)
                time.sleep(0.4)
                # Program top address
                dev.wait("Please Input : H", 0.2, offset_hex)
                time.sleep(0.4)
                dev.wait("please send ! (binary)", 0.2)

                # 送信：チャンクデータ
                #dev.sendfile(ipl_path + "/" + ipl_config["ipl"][soc][index]["name"])
                if not dev.write(chunk):
                    print("ERROR", f"Failed to send chunk data at offset {offset}.")
                    return False
                ex_list = ["Clear OK?(y/n)", ">"]
                res = dev.waitls(ex_list, 0.1, time_out=10)
                if res == 0:
                    dev.send("y")
                    time.sleep(0.2)
                    dev.sendln()
                    dev.sendln()

                # 次のオフセットに進める
                offset += len(chunk)
                # プログレスバーの更新
                if progress_flag:
                    progress_bar.update(len(chunk))
        return True
    except Exception as e:
        print("ERROR", f"Exception during file send: {e}")
        return False


####################################################################################################
# Help
####################################################################################################
def help(ipl_config):
    print("\tPlease use as syntax below:")
    print("\t python ipl_burning.py [SOC] [SERIAL] [PATH_TO_FILE_MOT] [PATH_TO_IPL_FILE] [OPTION]")
    print("\t    [SOC]          : %s" % ipl_config["flash_writer"].keys())
    print("\t    [SERIAL]       : %s " % [node.device for node in list(comports())])
    print("\t    [PATH_TO_FILE_MOT]")
    print("\t    [PATH_TO_IPL_FILE]")
    print("\t    [OPTION]:")
    print("\t       all               : download all of file ipl")
    exit(1)


####################################################################################################
# Main
####################################################################################################
def main(argv):
    with open("ipl_burning.json") as ipl_config_file:
        ipl_config = json.load(ipl_config_file)

    if len(argv) < 6:
        print_debug("ERROR", "Lack of argument")
        help(ipl_config)

    if str(argv[1]) not in ipl_config["flash_writer"].keys():
        print_debug("ERROR", 'in valid soc name "%s"' % str(argv[1]))
        help(ipl_config)
    SOC = str(argv[1])

    DEV_NODE = None
    nodes = list(comports())
    if sys.platform == "win32":
        serial_path = str(argv[2])
    else:
        serial_path = os.path.realpath(str(argv[2]))
    for node in nodes:
        if serial_path == node.device:
            DEV_NODE = str(argv[2])
            break

    if DEV_NODE is None:
        print_debug("ERROR", "%s is not exists" % str(argv[2]))
        help(ipl_config)

    if os.path.exists(str(argv[3] + "/" + ipl_config["flash_writer"][SOC])):
        MOT_DIR = str(argv[3])
    else:
        print_debug("ERROR", "%s/%s is not exists" % (argv[3], ipl_config["flash_writer"][SOC]))
        exit(1)

    if os.path.exists(str(argv[4])):
        IPL_DIR = str(argv[4])
    else:
        print_debug("ERROR", "%s is not exists" % str(argv[4]))
        help(ipl_config)

    # Define IPL shortened option
    if SOC == "sparrow-hawk":
        IPL_SHORTEN_OPTION = ["loader", "pciefw"]
    else:
        print_debug("ERROR", "%s is not supported" % SOC)

    OPTION = argv[5:]
    FILE_INFO_INDEX = []
    FILE_IPL_WILL_BURN = []
    FLASHADR_WILL_BURN = []
    WRITESEL_WILL_USE = []

    ret = 0

    # Load all ipl file
    if "all" in OPTION:
        OPTION = IPL_SHORTEN_OPTION

    for i in range(len(IPL_SHORTEN_OPTION)):
        if IPL_SHORTEN_OPTION[i] in OPTION:
            if not os.path.exists(IPL_DIR + "/" + ipl_config["ipl"][SOC][i]["name"]):
                ret = 1
                print_debug("ERROR", "%s is not exists" % ipl_config["ipl"][SOC][i]["name"])
                continue
            FILE_IPL_WILL_BURN.append(ipl_config["ipl"][SOC][i]["name"])
            FLASHADR_WILL_BURN.append(ipl_config["ipl"][SOC][i]["flash_addr"])
            WRITESEL_WILL_USE.append(ipl_config["ipl"][SOC][i]["write_sel"])
            FILE_INFO_INDEX.append(i)

    if ret == 1:
        exit(1)
    print_debug("INFO", "Download IPL for %s with serial device %s" % (SOC, DEV_NODE))
    print_debug("INFO", "File .mot at %s:" % MOT_DIR)
    print_debug("INFO", "    %s" % ipl_config["flash_writer"][SOC])
    print_debug("INFO", "Binary at %s:" % IPL_DIR)
    print_debug("INFO", "    %s" % FILE_IPL_WILL_BURN)
    print_debug("INFO", "flash address: \n\t\t%s" % FLASHADR_WILL_BURN)
    print_debug("INFO", "write select: \n\t\t%s" % WRITESEL_WILL_USE)

    # Serial config
    if SOC == "sparrow-hawk":
        BAUDRATE = 921600
    test_dev = Serial_conn(
        port=DEV_NODE,
        baudrate=BAUDRATE,
        bytesize=serial.EIGHTBITS,
        parity=serial.PARITY_NONE,
        stopbits=serial.STOPBITS_ONE,
    )

    if not test_dev.isOpen():
        test_dev.open()
    print_debug("INFO", "Monitor file sending...")
    sys.stdout.flush()
    test_dev.sendfile(MOT_DIR + "/" + ipl_config["flash_writer"][SOC])
    print_debug("INFO", "Send file .mot done")
    sys.stdout.flush()
    test_dev.wait(">", 0.2, "\r")

    # Loading srec
    test_dev.send("\n")

    for i in range(len(FILE_IPL_WILL_BURN)):
        if SOC == "sparrow-hawk":
            flash_burn_file_sparrow_hawk(test_dev, FILE_INFO_INDEX[i], IPL_DIR, SOC, ipl_config, FLASHADR_WILL_BURN[i])

    print_debug("INFO", "Download file .srec done")
    test_dev.close()


if __name__ == "__main__":
    main(sys.argv)
    exit(0)
