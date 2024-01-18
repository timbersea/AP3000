package com.an.entity.resp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SwipingCardResp extends BaseResp {
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

    @Override
    public byte[] data() {
        ByteBuf buffer = Unpooled.buffer(11);
        buffer.writeIntLE(cardId);
        buffer.writeByte(status);
        buffer.writeByte(feeType);
        buffer.writeIntLE(balanceValidateDate);
        buffer.writeByte(port);
        return buffer.array();
    }
}
