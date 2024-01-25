package com.an.net;

import com.an.entity.req.*;
import com.an.entity.resp.ChargePortOrderConfirmResp;
import com.an.service.MessageDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;

public class MessageHandler extends SimpleChannelInboundHandler<UDianPackage> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDianPackage msg) throws Exception {
        if(log.isDebugEnabled()){
            log.debug("physicalId: = [{}], msg = [{}]", ctx.channel().attr(GlobalContext.physicalIdAttr).get(), msg);
        }
        ctx.channel().attr(GlobalContext.physicalIdAttr).setIfAbsent(msg.getPhysicalId());
        GlobalContext.online(msg.getPhysicalId(),ctx);
        GlobalContext.completeResponse(msg.getMessageId(),msg);

        byte command = msg.getCommand();
        byte[] data = msg.getData();
        MessageDispatcher.getService(command);
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
                    System.arraycopy(data, 5 + portNum + portNum * 2, peakPower, 0, portNum * 2);
                }
                heatBeat.setPortStatus(portStatus);
                heatBeat.setCurrentPower(byte2shortLE(currentPower));
                heatBeat.setPeakPower(byte2shortLE(peakPower));
                heatBeat.setVirtualId(data[data.length - 5]);
                heatBeat.setSignalStrength(data[data.length - 4]);
                heatBeat.setDeviceType(data[data.length - 3]);
                heatBeat.setEnvironmentTemperature(data[data.length - 2]);
                heatBeat.setWorkPattern(data[data.length - 1]);

                ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                log.info(" data = [{}]", heatBeat);
                log.info(("deviceType :[{}] deviceCode[{}]"), msg.getDeviceType(), msg.getDeviceCode());
                break;
            }
            case 0x20: {
                Register register = new Register();
                register.setFirmwareVersion((short) ((data[1] << 8) | (data[0] & 0xff)));
                register.setPortNum(data[2]);
                register.setVirtualId(data[3]);
                register.setDeviceType(data[4]);
                register.setWorkPattern(data[5]);
                if (data.length == 8) {
                    register.setPowerVersion((short) ((data[7] << 8) | (data[6] & 0xff)));
                }

                ctx.writeAndFlush(msg.getReply(new byte[]{0}));

                log.info(" data = [{}]", register);
                break;
            }
            case 0x21: {
                HeatBeat21 heatBeat21 = new HeatBeat21();
                heatBeat21.setVoltage((short) ((data[1] << 8) | (data[0] & 0xff)));
                heatBeat21.setPortNum(data[2]);
                byte[] portStatus = new byte[data[2]];
                if (data[2] > 0) {
                    System.arraycopy(data, 3, portStatus, 0, data[2]);
                }
                heatBeat21.setPortStatus(portStatus);
                heatBeat21.setSignalStrength(data[data.length - 2]);
                heatBeat21.setEnvironmentTemperature(data[data.length - 1]);

                ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                log.info(" data = [{}]", heatBeat21);
                break;
            }
            case 0x22: {
                Integer number = (int) (System.currentTimeMillis() / 1000);
                byte[] byteArray = new byte[4];
                byteArray[0] = (byte) (number >> 24);
                byteArray[1] = (byte) (number >> 16);
                byteArray[2] = (byte) (number >> 8);
                byteArray[3] = (byte) (number & 0x000000FF);
                ctx.writeAndFlush(msg.getReply(byteArray));
                break;
            }
            case 0x02: {
                SwipingCard swipingCard = new SwipingCard();
                int result =
                        ((data[0] & 0xFF) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);//4字节大端序
                swipingCard.setCardId(result);
                swipingCard.setCardType(data[4]);
                swipingCard.setPort(data[5]);
                swipingCard.setBalance((short) ((data[7] << 8) | (data[6] & 0xff)));

                int timestamp =
                        (data[8] & 0xFF) | ((data[9] & 0xFF) << 8) | ((data[10] & 0xFF) << 16) | ((data[11] & 0xFF) << 24);//4字节小端序
                swipingCard.setTimestamp(timestamp);
                swipingCard.setCard2Length(data[12]);
                if (data[12] > 0) {
                    byte[] bytes = new byte[data[12]];
                    System.arraycopy(data, 13, bytes, 0, data[12]);
                    swipingCard.setCard2(bytes);
                }
                //TODO 根据实际业务回写数据
//                ctx.writeAndFlush(msg.getReply(new byte[]{0}));
//                SwipingCardResp swipingCardResp = new SwipingCardResp();
//                swipingCardResp.setCardId(swipingCard.getCardId());
//                swipingCardResp.set
                break;
            }
            case 0x03: {
                SettleConsume settleConsume = new SettleConsume();
                settleConsume.setChargeTime((short) ((data[1] << 8) | (data[0] & 0xff)));
                settleConsume.setMaxPower((short) ((data[3] << 8) | (data[2] & 0xff)));
                settleConsume.setElectric((short) ((data[5] << 8) | (data[4] & 0xff)));
                settleConsume.setPort(data[6]);
                settleConsume.setLunchMode(data[7]);
                settleConsume.setCardId((data[8] & 0xFF) | ((data[9] & 0xFF) << 8) | ((data[10] & 0xFF) << 16) | ((data[11] & 0xFF) << 24));
                settleConsume.setStopReason(data[12]);
                byte[] orderId = new byte[16];
                System.arraycopy(data, 12, orderId, 0, 16);
                settleConsume.setOrderId(DatatypeConverter.printHexBinary(orderId));
                settleConsume.setSecondMaxPower((short) ((data[12 + 7] << 8) | (data[12 + 6] & 0xff)));
                settleConsume.setTimestamp((data[12 + 8] & 0xFF) | ((data[12 + 9] & 0xFF) << 8) | ((data[12 + 10] & 0xFF) << 16) | ((data[12 + 11] & 0xFF) << 24));
                settleConsume.setOccupiedTime((short) ((data[12 + 13] << 8) | (data[12 + 12] & 0xff)));
                ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                log.info("data = [{}]", settleConsume);
                break;
            }
            case 0x04: {
                ChargePortOrderConfirm chargePortOrderConfirm = new ChargePortOrderConfirm();
                chargePortOrderConfirm.setPort(data[0]);
                chargePortOrderConfirm.setStatus(data[1]);
                chargePortOrderConfirm.setCardId(bytes2Int(Arrays.copyOfRange(data, 2, 6)));
                chargePortOrderConfirm.setChargeTime(byte2ShortValue(Arrays.copyOfRange(data, 7, 7 + 2)));
                chargePortOrderConfirm.setOrderId(DatatypeConverter.printHexBinary(Arrays.copyOfRange(data, 7, 7 + 16)));
                ChargePortOrderConfirmResp chargePortOrderConfirmResp = new ChargePortOrderConfirmResp();
                chargePortOrderConfirmResp.setReplay((byte) 0);
                chargePortOrderConfirmResp.setPort(chargePortOrderConfirm.getPort());
                ctx.writeAndFlush(msg.getReply(chargePortOrderConfirmResp.data()));
                log.info("data = [{}]", chargePortOrderConfirm);
                break;
            }
            case 0x06: {
                PortChargePowerHeatBeat portChargePowerHeatBeat = new PortChargePowerHeatBeat();
                ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
                portChargePowerHeatBeat.setPort(byteBuf.readByte());
                portChargePowerHeatBeat.setPortStatus(byteBuf.readByte());
                portChargePowerHeatBeat.setChargeTime(byteBuf.readShortLE());
                portChargePowerHeatBeat.setElectric(byteBuf.readShortLE());
                portChargePowerHeatBeat.setLunchMode(byteBuf.readByte());
                portChargePowerHeatBeat.setPower(byteBuf.readShortLE());
                portChargePowerHeatBeat.setMaxPower(byteBuf.readShortLE());
                portChargePowerHeatBeat.setMinPower(byteBuf.readShortLE());
                portChargePowerHeatBeat.setAvgPower(byteBuf.readShortLE());
                portChargePowerHeatBeat.setOrderId(DatatypeConverter.printHexBinary(byteBuf.readBytes(16).array()));
                portChargePowerHeatBeat.setTimeElectric(byteBuf.readShortLE());
                portChargePowerHeatBeat.setPeakPower(byteBuf.readShortLE());
                portChargePowerHeatBeat.setVoltage(byteBuf.readShortLE());
                portChargePowerHeatBeat.setElectricity(byteBuf.readShortLE());
                portChargePowerHeatBeat.setEnvironmentTemperature(byteBuf.readByte());
                portChargePowerHeatBeat.setPortTemperature(byteBuf.readByte());
                portChargePowerHeatBeat.setTimestamp(byteBuf.readIntLE());
                portChargePowerHeatBeat.setTakeTime(byteBuf.readShortLE());
                log.info("data = [{}]", portChargePowerHeatBeat);
                break;
            }
            case 0x42: {
                log.info("data = [{}]", data[0]);
                break;
            }
            case 0x43: {
                ChargeFinish chargeFinish = new ChargeFinish();
                ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
                chargeFinish.setChargeTime(byteBuf.readShortLE());
                chargeFinish.setMaxPower(byteBuf.readShortLE());
                chargeFinish.setElectric(byteBuf.readShortLE());
                chargeFinish.setPort(byteBuf.readByte());
                chargeFinish.setLunchMode(byteBuf.readByte());
                chargeFinish.setCardId(byteBuf.readIntLE());
                chargeFinish.setStopReason(byteBuf.readByte());
                chargeFinish.setOrderId(DatatypeConverter.printHexBinary(byteBuf.readBytes(16).array()));
                log.info("data = [{}]", chargeFinish);
                break;
            }
            case 0x44: {
                PortStatus portStatus = new PortStatus();
                ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
                portStatus.setPushType(byteBuf.readByte());
                portStatus.setPort(byteBuf.readByte());
                portStatus.setOrderId(DatatypeConverter.printHexBinary(byteBuf.readBytes(16).array()));
                break;
            }
            default:{
                log.debug("unknown command");
            }

        }

//            AbstractService service = MessageDispatcher.getService(command);
//            byte[] bytes = service.onReceive(data);
//            ctx.writeAndFlush(msg.getReply(bytes));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().attr(GlobalContext.activeTimestamp).setIfAbsent(System.currentTimeMillis());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        GlobalContext.offline(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("exceptionCaught:ctx = [{}], cause = [{}]", ctx, cause);
        ctx.close();
    }

    /**
     * 将字节数组按小端序每两个字节解析成一个short类型的值
     *
     * @param data
     * @return
     */
    private short[] byte2shortLE(byte[] data) {
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("array length must be even number!");
        }
        short[] shorts = new short[data.length / 2];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = ((short) ((data[i * 2 + 1] << 8) | (data[i * 2] & 0xff)));
        }
        return shorts;
    }

    private short byte2ShortValue(byte[] data) {
        if (data == null || data.length != 2) {
            throw new IllegalArgumentException("array length must be 2");
        }
        return (short) ((data[1] << 8) | (data[0] & 0xff));
    }

    /**
     * 4字节大端序转int
     *
     * @param data
     * @return
     */
    private int bytes2Int(byte[] data) {
        if (data == null || data.length != 4) {
            throw new IllegalArgumentException("byte array length must be 4");
        }
        return ((data[0] & 0xFF) << 24) | ((data[1] & 0xFF) << 16) | ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);//4字节大端序
    }

    private int bytes2IntLE(byte[] data) {
        if (data == null || data.length != 4) {
            throw new IllegalArgumentException("byte array length must be 4");
        }
        return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8) | ((data[2] & 0xFF) << 16) | ((data[3] & 0xFF) << 24);//4字节小端序
    }
}
