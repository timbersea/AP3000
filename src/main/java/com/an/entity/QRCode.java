package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 二维码设置结构体
@Data
public class QRCode implements Serializable {
    private byte mainType; // 主界面类型（Thrift i8 → Java byte）
    private byte[] reserveVaule; // 保留（Thrift binary → Java byte[]）
    private byte[] QRCode; // 二维码数据（Thrift binary → Java byte[]）
}
