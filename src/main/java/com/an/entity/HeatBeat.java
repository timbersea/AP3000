package com.an.entity;

import lombok.Data;

@Data
public class HeatBeat {

    /**
     * 固件版本
     */
    private short firmwareVersion;
    /**
     * 电压
     */
    private short voltage;
    /**
     * 端口数量
     */
    private byte portNum;
    /**
     * 各端口状态
     */
    private byte[] portStatus;

    /**
     * 各端口当前功率
     */
    private short[] currentPower;

    /**
     * 各端口峰值功率
     */
    private short[] peakPower;


    /**
     * 虚拟 ID
     */
    private byte virtualId;
    /**
     * 信号强度
     */
    private byte signalStrength;

    /**
     * 设备类型
     */
    private byte deviceType;

    /**
     * 当前环境温度
     */
    private byte environmentTemperature;

    /**
     * 工作模式
     */
    private byte workPattern;

}
