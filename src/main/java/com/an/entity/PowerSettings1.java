package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 运行参数1.1结构体
@Data
public class PowerSettings1 implements Serializable {
    private short pullOutPower; // 拔出功率（Thrift i16 → Java short）
    private short pullOutPowerRecognitionTime; // 拔出功率识别时间（Thrift i16 → Java short）
    private byte floatChargePercentage; // 浮充百分比（Thrift i8 → Java byte）
    private short floatChargeStatusRecognitionTime; // 浮充状态识别时间（Thrift i16 → Java short）
    private short floatChargeTime; // 浮充时间（Thrift i16 → Java short）
    private short heartbeatReportingInterval; // 心跳包上报间隔时间（Thrift i16 → Java short）
}