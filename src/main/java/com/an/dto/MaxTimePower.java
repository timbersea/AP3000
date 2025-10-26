package com.an.dto;

import lombok.Data;

import java.io.Serializable;

// 最大时长功率结构体
@Data
public class MaxTimePower implements Serializable {
    private int pileCode;

    private short maxChargeTime; // 最大充电时长（Thrift i16 → Java short）
    private short maxChargePower; // 最大充电功率（Thrift i16 → Java short）
}
