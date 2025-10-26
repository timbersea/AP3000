package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 充电开始响应结构体
@Data
public class StartChargeResp implements Serializable {
    private int pileCode;

    private byte resp; // 应答（Thrift i8 → Java byte）
    private byte[] orderId; // 订单编号（Thrift binary → Java byte[]）
    private byte port; // 端口号（Thrift i8 → Java byte）
    private short waitPort; // 待充端口（Thrift i16 → Java short）
}