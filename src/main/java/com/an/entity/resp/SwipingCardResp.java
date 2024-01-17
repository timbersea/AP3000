package com.an.entity.resp;

public class SwipingCardResp {
    private int cardId;
    private byte status;
    private byte feeType;
    private int balanceValidateDate;
    private byte port;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getFeeType() {
        return feeType;
    }

    public void setFeeType(byte feeType) {
        this.feeType = feeType;
    }

    public int getBalanceValidateDate() {
        return balanceValidateDate;
    }

    public void setBalanceValidateDate(int balanceValidateDate) {
        this.balanceValidateDate = balanceValidateDate;
    }

    public byte getPort() {
        return port;
    }

    public void setPort(byte port) {
        this.port = port;
    }
}
