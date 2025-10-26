package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 运行参数1.2结构体
@Data
public class PowerSettings2 implements Serializable {
    private short dynamicOverloadPower; // 动态过载功率（Thrift i16 → Java short）
    private short dynamicOverloadRecognitionTime; // 动态过载识别时间（Thrift i16 → Java short）
    private short dynamicOverloadStartTime; // 动态过载开始时间（Thrift i16 → Java short）
    private byte pullOutInterferencePower; // 拔出干扰功率（Thrift i8 → Java byte）
    private short pullOutInterferenceRecognitionTime; // 拔出干扰功率判断时间（Thrift i16 → Java short）
    private short floatChargeSecondRecognitionTime; // 浮充识别第二次时间点（Thrift i16 → Java short）
    private short floatChargeSecondStatusRecognitionTime; // 浮充状态第二次识别时间（Thrift i16 → Java short）
    private short minimumPower; // 最小功率（Thrift i16 → Java short）
    private short minimumPowerRecognitionTime; // 判断最小功率时间点（Thrift i16 → Java short）
    private short secondMaxPowerTime; // 第二最大功率时间点（Thrift i16 → Java short）
    private byte environmentalAlarmTemperature; // 环境报警温度（Thrift i8 → Java byte）
    private byte portAlarmTemperature; // 端口报警温度（Thrift i8 → Java byte）
    private byte openCloseDetectionUserPullOut; // 打开/关闭判断用户拔出（光耦）（Thrift i8 → Java byte）
    private byte qrCodeLight; // 二维码灯（Thrift i8 → Java byte）
}