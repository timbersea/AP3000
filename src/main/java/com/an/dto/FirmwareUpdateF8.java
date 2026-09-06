package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 固件升级结构体（F8指令）
@Data
public class FirmwareUpdateF8 implements Serializable {
    private int pileCode;

    private short totalPackage; // 总包数（Thrift i16 → Java short）
    private short currentPackage; // 当前包（Thrift i16 → Java short）
    private byte[] firmware; // 固件（Thrift binary → Java byte[]）
}
