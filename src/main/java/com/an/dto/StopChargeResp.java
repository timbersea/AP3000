package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 停止充电响应结构体
@Data
public class StopChargeResp implements Serializable {
    private int pileCode;

    private byte resp; // 应答（Thrift i8 → Java byte）
    private byte[] orderNo; // 订单编号（Thrift binary → Java byte[]）
}
