package com.an.entity;

import lombok.Data;

@Data
public class SwipingCardResp {
    private int cardId;
    private byte status;
    private byte feeType;
    private int balanceValidateDate;
    private byte port;

}
