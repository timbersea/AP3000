package com.an.entity.req;

public class SettleConsume {
    private  short chargeTime;
    private  short maxPower;
    private  short electric;
    private byte port;
    private  byte lunchMode;
    private int cardId;
    private byte stopReason;
    private String orderId;
    private short secondMaxPower;

    private int timestamp;

    private short occupiedTime;

    public short getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(short chargeTime) {
        this.chargeTime = chargeTime;
    }

    public short getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(short maxPower) {
        this.maxPower = maxPower;
    }

    public short getElectric() {
        return electric;
    }

    public void setElectric(short electric) {
        this.electric = electric;
    }

    public byte getPort() {
        return port;
    }

    public void setPort(byte port) {
        this.port = port;
    }

    public byte getLunchMode() {
        return lunchMode;
    }

    public void setLunchMode(byte lunchMode) {
        this.lunchMode = lunchMode;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public byte getStopReason() {
        return stopReason;
    }

    public void setStopReason(byte stopReason) {
        this.stopReason = stopReason;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public short getSecondMaxPower() {
        return secondMaxPower;
    }

    public void setSecondMaxPower(short secondMaxPower) {
        this.secondMaxPower = secondMaxPower;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public short getOccupiedTime() {
        return occupiedTime;
    }

    public void setOccupiedTime(short occupiedTime) {
        this.occupiedTime = occupiedTime;
    }

    @Override
    public String toString() {
        return "SettleConsume{" +
                "chargeTime=" + chargeTime +
                ", maxPower=" + maxPower +
                ", electric=" + electric +
                ", port=" + port +
                ", lunchMode=" + lunchMode +
                ", cardId=" + cardId +
                ", stopReason=" + stopReason +
                ", orderId='" + orderId + '\'' +
                ", secondMaxPower=" + secondMaxPower +
                ", timestamp=" + timestamp +
                ", occupiedTime=" + occupiedTime +
                '}';
    }
}
