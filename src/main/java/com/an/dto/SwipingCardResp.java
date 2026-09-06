package com.an.dto;

import lombok.Data;

@Data
public class SwipingCardResp {
    private int pileCode;

    private int cardId;
    private byte status;
    private byte feeType;
    private int balanceValidateDate;
    private byte port;

}
