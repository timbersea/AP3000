package com.an.service;

import com.an.entity.req.HeatBeat;

public class HeatBeatService extends AbstractService<HeatBeat> {
    @Override
    public byte[] onReceive(byte [] data) {
        HeatBeat heatBeat = parseData(data);
        return new byte[0];
    }

    @Override
    protected byte[] doService(HeatBeat heatBeat) {
        return new byte[]{0};
    }


    @Override
    public HeatBeat parseData(byte [] data){
        HeatBeat heatBeat = new HeatBeat();
        //小端转大端
        heatBeat.setFirmwareVersion((short) ((data[1] << 8) | (data[0] & 0xff)));
        heatBeat.setVoltage((short) ((data[3] << 8) | (data[2] & 0xff)));
        heatBeat.setPortNum(data[4]);
        byte portNum = heatBeat.getPortNum();
        byte[] portStatus = new byte[portNum];
        byte[] currentPower = new byte[portNum * 2];
        byte[] peakPower = new byte[portNum * 2];
        if (portNum > 0) {
            System.arraycopy(data, 5, portStatus, 0, portNum);
            System.arraycopy(data, 5 + portNum, currentPower, 0, portNum * 2);
            System.arraycopy(data, 5 + portNum+portNum*2, peakPower, 0, portNum * 2);
        }
        heatBeat.setPortStatus(portStatus);
        heatBeat.setCurrentPower(byte2short(currentPower));
        heatBeat.setPeakPower(byte2short(peakPower));
        heatBeat.setVirtualId(data[data.length - 5]);
        heatBeat.setSignalStrength(data[data.length - 4]);
        heatBeat.setDeviceType(data[data.length - 3]);
        heatBeat.setEnvironmentTemperature(data[data.length - 2]);
        heatBeat.setWorkPattern(data[data.length - 1]);
        return heatBeat;
    }

    /**
     * 将字节数组按小端序每两个字节解析成一个short类型的值
     * @param data
     * @return
     */
    private short[] byte2short(byte[] data) {
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("array length must be even number!");
        }
        short[] shorts = new short[data.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = ((short) ((data[i*2 + 1] << 8) | (data[i*2] & 0xff)));
        }
        return shorts;
    }
}
