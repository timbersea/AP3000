package com.an.dto;

import lombok.Data;

/**
 * 充电端口订单确认（04 指令）
 */
@Data
public class ChargePortOrderConfirm {
    private int pileCode;

    private byte port;
    private byte status;
    private int cardId;
    private short chargeTime;
    private String orderId;
}
