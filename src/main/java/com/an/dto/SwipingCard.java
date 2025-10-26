package com.an.dto;

import lombok.Data;

@Data
public class SwipingCard {
    private int pileCode;


    private int cardId;
    private byte cardType;
    private byte port;

    private short balance;
    private int timestamp;

    private byte card2Length;

    private byte [] card2;
}
