package com.an.entity.req;

public class ChargeOrderConfirm {
    private byte port;
    private byte launchMode;
    private String cardId;
    private short chargeTime;
    private String orderId;

    public byte getPort() {
        return port;
    }

    public void setPort(byte port) {
        this.port = port;
    }

    public byte getLaunchMode() {
        return launchMode;
    }

    public void setLaunchMode(byte launchMode) {
        this.launchMode = launchMode;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public short getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(short chargeTime) {
        this.chargeTime = chargeTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
