package com.an.common;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {
    private static Map<Integer, String> startChargeResponse = new HashMap<>();

    static {

        startChargeResponse.put(0, "执行成功（启动或停止充电）");
        startChargeResponse.put(1, "端口未插充电器（不执行）");
        startChargeResponse.put(2, "端口状态和充电命令相同（不执行）");
        startChargeResponse.put(3, "端口故障（执行）");
        startChargeResponse.put(4, "无此端口号（不执行）");
        startChargeResponse.put(5, "有多个待充端口（响应FF充电命令，不执行，只针对双路，双路设备的二维码只有一个，且端口号为FF，当2个口都插了充电器，服务器下发充电端口为FF时，设备检测到有2个口待充电，不知道充哪个，就会返回此应答）");
        startChargeResponse.put(6, "多路设备功率超标（不执行）");
        startChargeResponse.put(7, "存储器损坏");
        startChargeResponse.put(8, "（预检-继电器坏或保险丝断）");
        startChargeResponse.put(9, "（预检-继电器粘连）（执行给充电）");
        startChargeResponse.put(0x0A, "（预检-负载短路）");
        startChargeResponse.put(0x0B, "（烟感报警）");
    }

    private static final Map<Integer,String> stopReasonDescription =new HashMap<>(32);
    static {
        stopReasonDescription.put(1, "充满自停");
        stopReasonDescription.put(2, "达到最大充电时间");
        stopReasonDescription.put(3, "达到预设时间");
        stopReasonDescription.put(4, "达到预设电量");
        stopReasonDescription.put(5, "用户拔出");
        stopReasonDescription.put(6, "负载过大");
        stopReasonDescription.put(7, "服务器控制停止");
        stopReasonDescription.put(8, "动态过载");
        stopReasonDescription.put(9, "功率过小");
        stopReasonDescription.put(0xA, "环境温度过高 (仅适用于AP262、AP360)");
        stopReasonDescription.put(0xB, "端口温度过高 (仅适用于AP262)");
        stopReasonDescription.put(0xC, "过流");
        stopReasonDescription.put(0xD, "用户拔出-1，可能是插座弹片卡住");
        stopReasonDescription.put(0xE, "无功率停止，可能是接触不良或保险丝烧断故障");
        stopReasonDescription.put(0xF, "预检-继电器坏或保险丝断");
        stopReasonDescription.put(0x10, "水浸断电");
        stopReasonDescription.put(0x11, "灭火结算（本端口）");
        stopReasonDescription.put(0x12, "灭火结算（非本端口）");
        stopReasonDescription.put(0x13, "用户密码开柜断电");
        stopReasonDescription.put(0x14, "未关好柜门");
        stopReasonDescription.put(0x15, "外部操作停止");
        stopReasonDescription.put(0x16, "刷卡操作停止");
        stopReasonDescription.put(0x17, "服务器强制停止（主要用于充电柜强制开柜门）");
    }


    public static String getStartChargeResponse(int respCode) {
        return startChargeResponse.get(respCode);
    }

    public static String getStopReasonDescription(byte stopReason) {
        return stopReasonDescription.get(Integer.valueOf(stopReason));
    }

}
