package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 固件升级响应结构体
@Data
public class FirmwareUpdateResp implements Serializable {
    private int pileCode;

    private byte result; // 应答（Thrift i8 → Java byte）
    private short currentPackage; // 当前包（Thrift i16 → Java short）
}