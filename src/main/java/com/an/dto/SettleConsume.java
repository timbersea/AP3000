package com.an.dto;

import lombok.Data;

@Data
public class SettleConsume {
    private int pileCode;

    private short chargeTime;
    private short maxPower;
    private short electric;
    private byte port;
    private byte lunchMode;
    private int cardId;
    private byte stopReason;
    private String orderId;
    private short secondMaxPower;
    private int timestamp;
    private short occupiedTime;
}
