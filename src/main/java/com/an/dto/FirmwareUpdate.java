package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 固件升级结构体（E0/E1/E2指令）
@Data
public class FirmwareUpdate implements Serializable {
    private int pileCode;

    /**
     * 协议指令：E0 分机 / E1 电源板 / E2 主机统一；null 时服务层默认 E1
     */
    private Byte command;

    private short totalPackage; // 总包数（Thrift i16 → Java short）
    private short currentPackage; // 当前包（Thrift i16 → Java short）
    private byte[] firmware; // 固件（Thrift binary → Java byte[]）
    private long head; // 头字节（Thrift i64 → Java long）
    private int firmwareCheckSum; // 固件包的和校验（Thrift i32 → Java int）
    private byte deviceType; // 设备类型（Thrift i8 → Java byte）
    private short version; // 固件版本（Thrift i16 → Java short）
}