package com.an.dto;

import lombok.Data;

@Data
public class PortStatus {
    private int pileCode;

    private byte pushType;
    private byte port;
    private String orderId;
}
