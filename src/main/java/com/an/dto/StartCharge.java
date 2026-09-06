package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 充电开始请求结构体
@Data
public class StartCharge implements Serializable {
    private int pileCode;

    private byte feeType; // 费率模式（Thrift i8 → Java byte）
    private int balanceValidateDate; // 余额/有效期（Thrift i32 → Java int）
    private byte port; // 端口号（Thrift i8 → Java byte）
    private byte chargeCommand; // 充电命令（Thrift i8 → Java byte）
    private short chargeTimeElectric; // 充电时长/电量（Thrift i16 → Java short）
    private byte[] orderId; // 订单编号（Thrift binary → Java byte[]）
    private short maxChargeTime; // 最大充电时长（Thrift i16 → Java short）
    private short maxChargePower; // 最大充电功率（Thrift i16 → Java short）
    private byte QRCodeLight; // 二维码灯（Thrift i8 → Java byte）
    private byte longChargeMode; // 长充模式（Thrift i8 → Java byte）
    private short extraChargeTime; // 额外浮时间（Thrift i16 → Java short）
    private byte skipShortCircuitCheck; // 是否路过短路检测（Thrift i8 → Java byte）
}