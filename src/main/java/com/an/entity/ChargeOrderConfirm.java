package com.an.entity;

import lombok.Data;

@Data
public class ChargeOrderConfirm {
    private byte port;
    private byte launchMode;
    private String cardId;
    private short chargeTime;
    private String orderId;
}
