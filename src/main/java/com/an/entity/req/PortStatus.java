package com.an.entity.req;

import lombok.Data;

@Data
public class PortStatus {
    private byte pushType;
    private byte port;
    private String orderId;
}
