package com.an.dto;

import lombok.Data;

@Data
public class ChargeFinish {
    private int pileCode;

    private short chargeTime;
    private short maxPower;
    private short electric;
    private byte port;
    private byte lunchMode;
    private int cardId;
    private byte stopReason;
    private String orderId;
}
