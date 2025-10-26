package com.an.entity;


import lombok.Data;

import java.io.Serializable;

// 读取EEPROM请求结构体
@Data
public class ReadEEPROM implements Serializable {
    private short EEPROM; // EEPROM地址（Thrift i16 → Java short）
    private byte datalength; // 数据长度（Thrift i8 → Java byte）
}
