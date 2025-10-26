package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 读取EEPROM响应结构体
@Data
public class ReadEEPROMResp implements Serializable {
    private int pileCode;

    private byte isSuccess; // 是否成功（Thrift i8 → Java byte）
    private byte[] EEPROMData; // EEPROM数据（Thrift binary → Java byte[]）
}
