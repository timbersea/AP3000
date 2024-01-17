package com.an.entity.req;

import java.util.Arrays;

public class HeatBeat21 {
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

    public short getVoltage() {
        return voltage;
    }

    public void setVoltage(short voltage) {
        this.voltage = voltage;
    }

    public byte getPortNum() {
        return portNum;
    }

    public void setPortNum(byte portNum) {
        this.portNum = portNum;
    }

    public byte[] getPortStatus() {
        return portStatus;
    }

    public void setPortStatus(byte[] portStatus) {
        this.portStatus = portStatus;
    }

    public byte getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(byte signalStrength) {
        this.signalStrength = signalStrength;
    }

    public byte getEnvironmentTemperature() {
        return environmentTemperature;
    }

    public void setEnvironmentTemperature(byte environmentTemperature) {
        this.environmentTemperature = environmentTemperature;
    }

    @Override
    public String toString() {
        return "HeatBeat21{" +
                "voltage=" + voltage +
                ", portNum=" + portNum +
                ", portStatus=" + Arrays.toString(portStatus) +
                ", signalStrength=" + signalStrength +
                ", environmentTemperature=" + environmentTemperature +
                '}';
    }
}
