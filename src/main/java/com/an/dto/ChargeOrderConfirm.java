package com.an.dto;

import lombok.Data;

@Data
public class ChargeOrderConfirm {
    private int pileCode;

    private byte port;
    private byte launchMode;
    private String cardId;
    private short chargeTime;
    private String orderId;
}
