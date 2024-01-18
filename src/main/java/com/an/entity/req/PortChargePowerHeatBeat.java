package com.an.entity.req;

import lombok.Data;

@Data
public class PortChargePowerHeatBeat {
    private byte port;//端口号
    private byte portStatus;//端口状态
    private short chargeTime;//充电时长
    private short electric;//累计电量
    private byte lunchMode;//在线/离线启动/验证码
    private short power;//实时功率
    private short maxPower;//最大功率
    private short minPower;//最小功率
    private short avgPower;//平均功率
    private String orderId;//订单ID
    private short timeElectric;//该时间段内消耗的电量
    private  short peakPower;//峰值功率
    private short voltage;//电压
    private short electricity;//电流
    private byte environmentTemperature;//环境温度
    private byte portTemperature;//端口温度
    private int timestamp;//时间戳
    private short takeTime;//占位时长
}
