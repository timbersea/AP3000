package com.an.entity;

import lombok.Data;

@Data
public class Register {
    /**
     * 固件版本
     */
    private short firmwareVersion;

    /**
     * 端口数量
     */
    private byte portNum;

    /**
     * 虚拟 ID
     */
    private byte virtualId;


    /**
     * 设备类型
     */
    private byte deviceType;

    /**
     * 工作模式
     */
    private byte workPattern;

    /**
     * 电源板版本号
     */
    private short powerVersion;

}
