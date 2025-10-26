package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 用户卡参数结构体
@Data
public class UserCard implements Serializable {
    private byte userSector; // 用户卡扇区（Thrift i8 → Java byte）
    private byte[] uesrCardPassword; // 用户卡密码（Thrift binary → Java byte[]）
    private byte[] newCardPassword; // 新密码（Thrift binary → Java byte[]）
}