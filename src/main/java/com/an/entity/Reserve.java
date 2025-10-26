package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 保留占位指令结构体
@Data
public class Reserve implements Serializable {
    private int R1; // 保留字段1（Thrift i32 → Java int）
    private int R2; // 保留字段2（Thrift i32 → Java int）
    private int R3; // 保留字段3（Thrift i32 → Java int）
}

