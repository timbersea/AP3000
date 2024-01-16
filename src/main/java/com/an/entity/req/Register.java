package com.an.entity.req;

public class Register {
    /**
     * 固件版本
     */
    private short firmwareVersion;

    /**
     * 端口数量
     */
    private byte portNum;

    /**
     * 虚拟 ID
     */
    private byte virtualId;


    /**
     * 设备类型
     */
    private byte deviceType;

    /**
     * 工作模式
     */
    private byte workPattern;

    /**
     * 电源板版本号
     */
    private short powerVersion;

    public short getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(short firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public byte getPortNum() {
        return portNum;
    }

    public void setPortNum(byte portNum) {
        this.portNum = portNum;
    }

    public byte getVirtualId() {
        return virtualId;
    }

    public void setVirtualId(byte virtualId) {
        this.virtualId = virtualId;
    }

    public byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public byte getWorkPattern() {
        return workPattern;
    }

    public void setWorkPattern(byte workPattern) {
        this.workPattern = workPattern;
    }

    public short getPowerVersion() {
        return powerVersion;
    }

    public void setPowerVersion(short powerVersion) {
        this.powerVersion = powerVersion;
    }

    @Override
    public String toString() {
        return "Register{" +
                "firmwareVersion=" + firmwareVersion +
                ", portNum=" + portNum +
                ", virtualId=" + virtualId +
                ", deviceType=" + deviceType +
                ", workPattern=" + workPattern +
                ", powerVersion=" + powerVersion +
                '}';
    }
}
