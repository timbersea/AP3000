package com.an.entity;

import lombok.Data;

/**
 * 充电端口订单确认（04 指令）
 */
@Data
public class ChargePortOrderConfirm {
    private byte port;
    private byte status;
    private int cardId;
    private short chargeTime;
    private String orderId;
}
