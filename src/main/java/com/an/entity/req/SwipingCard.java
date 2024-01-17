package com.an.entity.req;

public class SwipingCard {
    private int cardId;
    private byte cardType;
    private byte port;

    private short balance;
    private int timestamp;

    private byte card2Length;

    private byte [] card2;

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public byte getCardType() {
        return cardType;
    }

    public void setCardType(byte cardType) {
        this.cardType = cardType;
    }

    public byte getPort() {
        return port;
    }

    public void setPort(byte port) {
        this.port = port;
    }

    public short getBalance() {
        return balance;
    }

    public void setBalance(short balance) {
        this.balance = balance;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public byte getCard2Length() {
        return card2Length;
    }

    public void setCard2Length(byte card2Length) {
        this.card2Length = card2Length;
    }

    public byte[] getCard2() {
        return card2;
    }

    public void setCard2(byte[] card2) {
        this.card2 = card2;
    }
}
