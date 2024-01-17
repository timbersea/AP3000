package com.an.net;

import com.an.entity.req.HeatBeat;
import com.an.entity.req.Register;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<UDianPackage> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDianPackage msg) throws Exception {
        log.debug("channelRead0:ctx = [{}], msg = [{}]", ctx, msg);
        int command = msg.getCommand();
        byte[] data = msg.getData();
        switch (command) {
            case 0x01: {
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
                log.info(" data = [{}]", heatBeat);
                break;
            }
            case 0x20: {
                Register register = new Register();
                register.setFirmwareVersion((short) ((data[1] << 8) | (data[0] & 0xff)));
                register.setPortNum(data[2]);
                register.setVirtualId(data[3]);
                register.setDeviceType(data[4]);
                register.setWorkPattern(data[5]);
                register.setPowerVersion((short) ((data[7] << 8) | (data[6] & 0xff)));

                log.info(" data = [{}]", register);
            }
            case 0x22: {
                UDianPackage uDianPackage = new UDianPackage();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
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
