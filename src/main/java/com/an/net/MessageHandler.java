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
                heatBeat.setPortNum(data[5]);
                byte[] portStatus = new byte[heatBeat.getPortNum()];
                if (heatBeat.getPortNum() >= 0) {
                    System.arraycopy(data, 6, portStatus, 0, heatBeat.getPortNum());
                }
                heatBeat.setPortStatus(portStatus);
                //TODO set currentPower ,set peakPower
                heatBeat.setVirtualId(data[data.length - 5]);
                heatBeat.setSignalStrength(data[data.length - 4]);
                heatBeat.setDeviceType(data[data.length - 3]);
                heatBeat.setEnvironmentTemperature(data[data.length - 2]);
                heatBeat.setWorkPattern(data[data.length - 1]);
                //heatBeat.setCurrentPower(portCurrentPower);
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
            case 0x22:{
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
}
