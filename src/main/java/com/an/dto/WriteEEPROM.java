package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 写入EEPROM结构体
@Data
public class WriteEEPROM implements Serializable {
    private int pileCode;

    private short EEPROM; // EEPROM地址（Thrift i16 → Java short）
    private byte datalength; // 数据长度（Thrift i8 → Java byte）
    private byte[] EEPROMDATA; // EEPROM数据（Thrift binary → Java byte[]）
}
