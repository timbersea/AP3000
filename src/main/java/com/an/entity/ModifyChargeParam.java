package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 修改充电参数结构体
@Data
public class ModifyChargeParam implements Serializable {
    private byte feeType; // 费率模式（Thrift i8 → Java byte）
    private byte port; // 端口号（Thrift i8 → Java byte）
    private short chargeTimeEnerge; // 充电时长/电量（Thrift i16 → Java short）
}