package com.an.dto;

import lombok.Data;

@Data
public class HeatBeat21 {
    private int pileCode;


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
     * 信号强度
     */
    private byte signalStrength;


    /**
     * 当前环境温度
     */
    private byte environmentTemperature;

}
