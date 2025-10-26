package com.an.net;

import com.an.dto.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.an.net.GlobalContext.pileCodeAttr;

@Component
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<UDianPackage> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDianPackage msg) throws Exception {
        int pileCode = msg.physicalId2PileCode();
        ctx.channel().attr(pileCodeAttr).setIfAbsent(pileCode);
        ctx.channel().attr(GlobalContext.deviceTypeAttr).setIfAbsent(msg.physicalId2Type());
        GlobalContext.online(pileCode, ctx);
        GlobalContext.completeResponse(msg.getMessageId(), msg);

        int command = msg.getCommand();
        byte[] data = msg.getData();
        ByteBuf byteBufData = Unpooled.buffer(data.length);
        byteBufData.writeBytes(data);
        // MessageDispatcher.getService(command);
        try {
            //各个包的的字段详细见文档AP3000第二版-设备与服务器通信协议.pdf
            switch (command) {
                //心跳包
                case 0x01: {
                    HeatBeat heatBeat = new HeatBeat();
                    heatBeat.setPileCode(pileCode);
                    //小端转大端
                    heatBeat.setFirmwareVersion(byteBufData.readShortLE());
                    heatBeat.setVoltage(byteBufData.readShortLE());
                    heatBeat.setPortNum(byteBufData.readByte());
                    byte portNum = heatBeat.getPortNum();
                    byte[] portStatus = new byte[portNum];
                    short [] currentPower = new short[portNum];;
                    short [] peakPower = new short[portNum * 2];

                    for (int i = 0; i < portNum; i++) {
                        portStatus[i] = byteBufData.readByte();
                    }
                    for (int i = 0; i < portNum; i++) {
                        currentPower[i]=(byteBufData.readShortLE());
                    }
                    for (int i = 0; i < portNum; i++) {
                        peakPower[i]=(byteBufData.readShortLE());
                    }

                    heatBeat.setPortStatus(portStatus);
                    heatBeat.setCurrentPower(currentPower);
                    heatBeat.setPeakPower(peakPower);
                    heatBeat.setVirtualId(byteBufData.readByte());
                    heatBeat.setSignalStrength(byteBufData.readByte());
                    heatBeat.setDeviceType(byteBufData.readByte());
                    heatBeat.setEnvironmentTemperature(byteBufData.readByte());
                    heatBeat.setWorkPattern(byteBufData.readByte());

                    ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                    log.info(" data = [{}]", heatBeat);
                    log.info(("deviceType :[{}] pileCode[{}]"), msg.getDeviceType(), msg.getPileCode());
                    break;
                }
                //注册消息
                case 0x20: {
                    Register register = new Register();
                    register.setPileCode(pileCode);
                    register.setFirmwareVersion(byteBufData.readShortLE());
                    register.setPortNum(byteBufData.readByte());
                    register.setVirtualId(byteBufData.readByte());
                    register.setDeviceType(byteBufData.readByte());
                    register.setWorkPattern(byteBufData.readByte());
                    if (byteBufData.readableBytes() >= 2) {
                        register.setPowerVersion(byteBufData.readShortLE());
                    }

                    log.info(" data = [{}]", register);
                    ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                    break;
                }
                //21，注册消息的一种
                case 0x21: {
                    HeatBeat21 heatBeat21 = new HeatBeat21();
                    heatBeat21.setPileCode(pileCode);

                    heatBeat21.setVoltage(byteBufData.readShortLE());
                    heatBeat21.setPortNum(byteBufData.readByte());
                    byte[] portStatus = new byte[heatBeat21.getPortNum()];
                    if (heatBeat21.getPortNum() > 0) {
                        for (int i = 0; i < heatBeat21.getPortNum(); i++) {
                            portStatus[i] = byteBufData.readByte();
                        }
                    }
                    heatBeat21.setPortStatus(portStatus);
                    heatBeat21.setSignalStrength(byteBufData.readByte());
                    heatBeat21.setEnvironmentTemperature(byteBufData.readByte());

                    log.info(" data = [{}]", heatBeat21);
                    ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                    break;
                }
                //获取时间
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
                //收到设备上报的刷卡消息
                case 0x02: {
                    SwipingCard swipingCard = new SwipingCard();
                    swipingCard.setPileCode(pileCode);
                    swipingCard.setCardId(byteBufData.readInt());
                    swipingCard.setCardType(byteBufData.readByte());
                    swipingCard.setPort(byteBufData.readByte());
                    swipingCard.setBalance(byteBufData.readShortLE());

                    swipingCard.setTimestamp(byteBufData.readIntLE());
                    swipingCard.setCard2Length(byteBufData.readByte());
                    if (swipingCard.getCard2Length() > 0) {
                        byte[] bytes = new byte[swipingCard.getCard2Length()];
                        byteBufData.readBytes(bytes);
                        swipingCard.setCard2(bytes);
                    }
                    SwipingCardResp swipingCardResp = new SwipingCardResp();
                    ByteBuf buffer = Unpooled.buffer(11);
                    buffer.writeIntLE(swipingCard.getCardId());
                    buffer.writeByte(swipingCard.getCardType());
                    buffer.writeByte(swipingCardResp.getFeeType());
                    buffer.writeIntLE(swipingCardResp.getBalanceValidateDate());
                    buffer.writeByte(swipingCard.getPort());
                    ctx.writeAndFlush(msg.getReply(buffer.array()));
                    break;
                }
                //订单结算消息
                case 0x03: {
                    SettleConsume settleConsume = new SettleConsume();
                    settleConsume.setPileCode(pileCode);
                    settleConsume.setChargeTime(byteBufData.readShortLE());
                    settleConsume.setMaxPower(byteBufData.readShortLE());
                    settleConsume.setElectric(byteBufData.readShortLE());
                    settleConsume.setPort(byteBufData.readByte());
                    settleConsume.setLunchMode(byteBufData.readByte());
                    settleConsume.setCardId(byteBufData.readInt());
                    settleConsume.setStopReason(byteBufData.readByte());
                    byteBufData.skipBytes(8);
                    settleConsume.setOrderId(String.valueOf(byteBufData.readLongLE()));
                    settleConsume.setSecondMaxPower(byteBufData.readShortLE());
                    if (byteBufData.readableBytes() >= 4) {
                        settleConsume.setTimestamp(byteBufData.readIntLE());
                    }
                    if (byteBufData.readableBytes() >= 2) {
                        settleConsume.setOccupiedTime(byteBufData.readShortLE());
                    }
                    ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                    log.info("data = [{}]", settleConsume);
                    break;
                }
                //充电订单确认
                case 0x04: {
                    ChargePortOrderConfirm chargePortOrderConfirm = new ChargePortOrderConfirm();
                    chargePortOrderConfirm.setPileCode(pileCode);
                    chargePortOrderConfirm.setPort(byteBufData.readByte());
                    chargePortOrderConfirm.setStatus(byteBufData.readByte());
                    chargePortOrderConfirm.setCardId(byteBufData.readInt());
                    chargePortOrderConfirm.setChargeTime(byteBufData.readShortLE());
                    byteBufData.skipBytes(8);
                    chargePortOrderConfirm.setOrderId(byteBufData.readLongLE() + "");
                    log.info("data = [{}]", chargePortOrderConfirm);
                    ctx.writeAndFlush(msg.getReply(new byte[]{chargePortOrderConfirm.getPort(), 0}));
                    break;
                }
                //充电口功率心跳数据
                case 0x06: {
                    PortChargePowerHeatBeat portChargePowerHeatBeat = new PortChargePowerHeatBeat();
                    portChargePowerHeatBeat.setPileCode(pileCode);
                    portChargePowerHeatBeat.setPort(byteBufData.readByte());
                    portChargePowerHeatBeat.setPortStatus(byteBufData.readByte());
                    portChargePowerHeatBeat.setChargeTime(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setElectric(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setLunchMode(byteBufData.readByte());
                    portChargePowerHeatBeat.setPower(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setMaxPower(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setMinPower(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setAvgPower(byteBufData.readShortLE());
                    byteBufData.skipBytes(8);
                    portChargePowerHeatBeat.setOrderId(String.valueOf(byteBufData.readLongLE()));
                    portChargePowerHeatBeat.setTimeElectric(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setPeakPower(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setVoltage(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setElectricity(byteBufData.readShortLE());
                    portChargePowerHeatBeat.setEnvironmentTemperature(byteBufData.readByte());
                    portChargePowerHeatBeat.setPortTemperature(byteBufData.readByte());
                    portChargePowerHeatBeat.setTimestamp(byteBufData.readIntLE());
                    if (byteBufData.readableBytes() >= 2) {
                        portChargePowerHeatBeat.setTakeTime(byteBufData.readShortLE());
                    }
                    log.info("data = [{}]", portChargePowerHeatBeat);
                    break;
                }
                case 0x42: {
                    log.info("data = [{}] {}", byteBufData.readByte(), byteBufData.readByte());
                    break;
                }
                //订单结束
                case 0x43: {
                    ChargeFinish chargeFinish = new ChargeFinish();
                    chargeFinish.setPileCode(pileCode);
                    chargeFinish.setChargeTime(byteBufData.readShortLE());
                    chargeFinish.setMaxPower(byteBufData.readShortLE());
                    chargeFinish.setElectric(byteBufData.readShortLE());
                    chargeFinish.setPort(byteBufData.readByte());
                    chargeFinish.setLunchMode(byteBufData.readByte());
                    chargeFinish.setCardId(byteBufData.readIntLE());
                    chargeFinish.setStopReason(byteBufData.readByte());
                    byteBufData.skipBytes(8);
                    chargeFinish.setOrderId(byteBufData.readLongLE() + "");
                    log.info("data = [{}]", chargeFinish);
                    break;
                }
                //充电口状态
                case 0x44: {
                    PortStatus portStatus = new PortStatus();
                    portStatus.setPileCode(pileCode);
                    portStatus.setPushType(byteBufData.readByte());
                    portStatus.setPort(byteBufData.readByte());
                    byteBufData.skipBytes(8);
                    portStatus.setOrderId(byteBufData.readLongLE() + "");
                    break;
                }

                //0x82= 无符号的130，补码为-126
                case -126: {
                    byte response = byteBufData.readByte();
                    byteBufData.skipBytes(8);
                    long orderNo = byteBufData.readLongLE();
                    byte port = byteBufData.readByte();
                    byte waitPort = byteBufData.readByte();
                    log.info("0x82 :response:[{}] orderNo:[{}] port:[{}] waitPort:[{}]", response, orderNo, port, waitPort);
                    break;
                }
                default: {
                    log.debug("unknown command [{}]", msg);
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(byteBufData);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().attr(GlobalContext.activeTimestamp).setIfAbsent(System.currentTimeMillis());
        ctx.executor().schedule(() -> {
            if(ctx.channel().attr(GlobalContext.pileCodeAttr).get()==null){
                log.warn("ctx :[{}] no physicalId after connected for 30 seconds,will be close", ctx);
                ctx.close();
            }
        },30, TimeUnit.SECONDS);
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
        log.error("exceptionCaught:ctx = [{}], cause = [{}]", ctx, cause);
        Integer pileCode = ctx.channel().attr(pileCodeAttr).get();
        if (pileCode != null) {
            log.info("offline: pileCode:[{}]  channelHandlerContext = [{}]", pileCode, ctx);
        }
        ctx.close();
    }
}
