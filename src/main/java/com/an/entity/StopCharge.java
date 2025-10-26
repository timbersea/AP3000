package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 停止充电请求结构体
@Data
public class StopCharge implements Serializable {
    private byte port; // 端口号（Thrift i8 → Java byte）
    private byte[] orderNo; // 订单编号（Thrift binary → Java byte[]）
}