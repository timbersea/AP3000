package com.an.entity;

import lombok.Data;

@Data
public class PortStatus {
    private byte pushType;
    private byte port;
    private String orderId;
}
