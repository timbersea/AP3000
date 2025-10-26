package com.an.entity;

import lombok.Data;

import java.io.Serializable;

// 语音播放结构体
@Data
public class Voice implements Serializable {
    private byte allowBreak; // 是否打断（Thrift i8 → Java byte，原字段名break改为isBreak避免关键字冲突）
    private byte voiceLength; // 语音长度（Thrift i8 → Java byte）
    private byte[] voiceCombination; // 语音组合（Thrift binary → Java byte[]）
}
