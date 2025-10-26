package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 多功能指令结构体
@Data
public class MutiFunction implements Serializable {
    private byte function; // 功能（Thrift i8 → Java byte）
    private byte port; // 端口号（Thrift i8 → Java byte）
}
