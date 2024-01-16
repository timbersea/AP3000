package com.an.entity.req;

import java.util.Arrays;

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

    public short getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(short firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

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

    public short[] getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(short[] currentPower) {
        this.currentPower = currentPower;
    }

    public short[] getPeakPower() {
        return peakPower;
    }

    public void setPeakPower(short[] peakPower) {
        this.peakPower = peakPower;
    }

    public byte getVirtualId() {
        return virtualId;
    }

    public void setVirtualId(byte virtualId) {
        this.virtualId = virtualId;
    }

    public byte getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(byte signalStrength) {
        this.signalStrength = signalStrength;
    }

    public byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public byte getEnvironmentTemperature() {
        return environmentTemperature;
    }

    public void setEnvironmentTemperature(byte environmentTemperature) {
        this.environmentTemperature = environmentTemperature;
    }

    public byte getWorkPattern() {
        return workPattern;
    }

    public void setWorkPattern(byte workPattern) {
        this.workPattern = workPattern;
    }

    @Override
    public String toString() {
        return "HeatBeat{" +
                "firmwareVersion=" + firmwareVersion +
                ", voltage=" + voltage +
                ", portNum=" + portNum +
                ", portStatus=" + Arrays.toString(portStatus) +
                ", currentPower=" + Arrays.toString(currentPower) +
                ", peakPower=" + Arrays.toString(peakPower) +
                ", virtualId=" + virtualId +
                ", signalStrength=" + signalStrength +
                ", deviceType=" + deviceType +
                ", environmentTemperature=" + environmentTemperature +
                ", workPattern=" + workPattern +
                '}';
    }
}
