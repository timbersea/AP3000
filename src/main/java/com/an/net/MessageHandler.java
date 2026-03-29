package com.an.net;

import com.an.dto.ChargeFinish;
import com.an.dto.ChargePortOrderConfirm;
import com.an.dto.HeatBeat;
import com.an.dto.HeatBeat21;
import com.an.dto.PortChargePowerHeatBeat;
import com.an.dto.PortStatus;
import com.an.dto.Register;
import com.an.dto.SettleConsume;
import com.an.dto.SwipingCard;
import com.an.dto.SwipingCardResp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.an.net.GlobalContext.pileCodeAttr;
import static com.an.net.UDianPackage.byteBufZero;

@Component
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<UDianPackage> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UDianPackage msg) {
        int pileCode = msg.physicalId2PileCode();
        ctx.channel().attr(pileCodeAttr).setIfAbsent(pileCode);
        ctx.channel().attr(GlobalContext.deviceTypeAttr).setIfAbsent(msg.physicalId2Type());
        GlobalContext.online(pileCode, ctx);
        GlobalContext.completeResponse(msg.getMessageId(), msg);

        int command = msg.getCommand();
        ByteBuf data = Unpooled.copiedBuffer(msg.getData());
        // MessageDispatcher.getService(command);
        try {
            //各个包的的字段详细见文档AP3000第二版-设备与服务器通信协议.pdf
            switch (command) {
                //心跳包
                case 0x01: {
                    HeatBeat heatBeat = new HeatBeat();
                    heatBeat.setPileCode(pileCode);
                    //小端转大端
                    heatBeat.setFirmwareVersion(data.readShortLE());
                    heatBeat.setVoltage(data.readShortLE());
                    heatBeat.setPortNum(data.readByte());
                    byte portNum = heatBeat.getPortNum();
                    byte[] portStatus = new byte[portNum];
                    short[] currentPower = new short[portNum];
                    short[] peakPower = new short[portNum * 2];

                    for (int i = 0; i < portNum; i++) {
                        portStatus[i] = data.readByte();
                    }
                    for (int i = 0; i < portNum; i++) {
                        currentPower[i] = (data.readShortLE());
                    }
                    for (int i = 0; i < portNum; i++) {
                        peakPower[i] = (data.readShortLE());
                    }

                    heatBeat.setPortStatus(portStatus);
                    heatBeat.setCurrentPower(currentPower);
                    heatBeat.setPeakPower(peakPower);
                    heatBeat.setVirtualId(data.readByte());
                    heatBeat.setSignalStrength(data.readByte());
                    heatBeat.setDeviceType(data.readByte());
                    heatBeat.setEnvironmentTemperature(data.readByte());
                    heatBeat.setWorkPattern(data.readByte());

                    ctx.writeAndFlush(msg.getReply(byteBufZero()));
                    log.info(" data = [{}]", heatBeat);
                    log.info(("deviceType :[{}] pileCode[{}]"), msg.getDeviceType(), msg.getPileCode());
                    break;
                }
                //注册消息
                case 0x20: {
                    Register register = new Register();
                    register.setPileCode(pileCode);
                    register.setFirmwareVersion(data.readShortLE());
                    register.setPortNum(data.readByte());
                    register.setVirtualId(data.readByte());
                    register.setDeviceType(data.readByte());
                    register.setWorkPattern(data.readByte());
                    if (data.readableBytes() >= 2) {
                        register.setPowerVersion(data.readShortLE());
                    }

                    log.info(" data = [{}]", register);
                    ctx.writeAndFlush(msg.getReply(byteBufZero()));
                    break;
                }
                //21，注册消息的一种
                case 0x21: {
                    HeatBeat21 heatBeat21 = new HeatBeat21();
                    heatBeat21.setPileCode(pileCode);

                    heatBeat21.setVoltage(data.readShortLE());
                    heatBeat21.setPortNum(data.readByte());
                    byte[] portStatus = new byte[heatBeat21.getPortNum()];
                    if (heatBeat21.getPortNum() > 0) {
                        for (int i = 0; i < heatBeat21.getPortNum(); i++) {
                            portStatus[i] = data.readByte();
                        }
                    }
                    heatBeat21.setPortStatus(portStatus);
                    heatBeat21.setSignalStrength(data.readByte());
                    heatBeat21.setEnvironmentTemperature(data.readByte());

                    log.info(" data = [{}]", heatBeat21);
                    ctx.writeAndFlush(msg.getReply(byteBufZero()));
                    break;
                }
                //获取时间
                case 0x22: {
                    int number = (int) (System.currentTimeMillis() / 1000);
                    ByteBuf byteArray = Unpooled.buffer(4);
                    byteArray.writeByte(number & 0x000000FF);
                    byteArray.writeByte(number >> 8);
                    byteArray.writeByte(number >> 16);
                    byteArray.writeByte(number >> 24);
                    ctx.writeAndFlush(msg.getReply(byteArray.array()));
                    break;
                }
                //收到设备上报的刷卡消息
                case 0x02: {
                    SwipingCard swipingCard = new SwipingCard();
                    swipingCard.setPileCode(pileCode);
                    swipingCard.setCardId(data.readInt());
                    swipingCard.setCardType(data.readByte());
                    swipingCard.setPort(data.readByte());
                    swipingCard.setBalance(data.readShortLE());
                    if (data.readableBytes() >= 4) {
                        swipingCard.setTimestamp(data.readIntLE());
                    }
                    swipingCard.setCard2Length(data.readByte());
                    if (swipingCard.getCard2Length() > 0) {
                        byte[] bytes = new byte[swipingCard.getCard2Length()];
                        data.readBytes(bytes);
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
                    settleConsume.setChargeTime(data.readShortLE());
                    settleConsume.setMaxPower(data.readShortLE());
                    settleConsume.setElectric(data.readShortLE());
                    settleConsume.setPort(data.readByte());
                    settleConsume.setLunchMode(data.readByte());
                    settleConsume.setCardId(data.readInt());
                    settleConsume.setStopReason(data.readByte());
                    data.skipBytes(8);
                    settleConsume.setOrderId(String.valueOf(data.readLongLE()));
                    settleConsume.setSecondMaxPower(data.readShortLE());
                    if (data.readableBytes() >= 4) {
                        settleConsume.setTimestamp(data.readIntLE());
                    }
                    if (data.readableBytes() >= 2) {
                        settleConsume.setOccupiedTime(data.readShortLE());
                    }
                    ctx.writeAndFlush(msg.getReply(byteBufZero()));
                    log.info("data = [{}]", settleConsume);
                    break;
                }
                //充电订单确认
                case 0x04: {
                    ChargePortOrderConfirm chargePortOrderConfirm = new ChargePortOrderConfirm();
                    chargePortOrderConfirm.setPileCode(pileCode);
                    chargePortOrderConfirm.setPort(data.readByte());
                    chargePortOrderConfirm.setStatus(data.readByte());
                    chargePortOrderConfirm.setCardId(data.readInt());
                    chargePortOrderConfirm.setChargeTime(data.readShortLE());
                    data.skipBytes(8);
                    chargePortOrderConfirm.setOrderId(data.readLongLE() + "");
                    log.info("data = [{}]", chargePortOrderConfirm);
                    ByteBuf buffer = Unpooled.buffer(2);
                    buffer.writeByte(chargePortOrderConfirm.getPort());
                    buffer.writeByte(0);
                    ctx.writeAndFlush(msg.getReply(buffer.array()));
                    break;
                }
                //充电口功率心跳数据
                case 0x06: {
                    PortChargePowerHeatBeat portChargePowerHeatBeat = new PortChargePowerHeatBeat();
                    portChargePowerHeatBeat.setPileCode(pileCode);
                    portChargePowerHeatBeat.setPort(data.readByte());
                    portChargePowerHeatBeat.setPortStatus(data.readByte());
                    portChargePowerHeatBeat.setChargeTime(data.readShortLE());
                    portChargePowerHeatBeat.setElectric(data.readShortLE());
                    portChargePowerHeatBeat.setLunchMode(data.readByte());
                    portChargePowerHeatBeat.setPower(data.readShortLE());
                    portChargePowerHeatBeat.setMaxPower(data.readShortLE());
                    portChargePowerHeatBeat.setMinPower(data.readShortLE());
                    portChargePowerHeatBeat.setAvgPower(data.readShortLE());
                    data.skipBytes(8);
                    portChargePowerHeatBeat.setOrderId(String.valueOf(data.readLongLE()));
                    portChargePowerHeatBeat.setTimeElectric(data.readShortLE());
                    portChargePowerHeatBeat.setPeakPower(data.readShortLE());
                    portChargePowerHeatBeat.setVoltage(data.readShortLE());
                    portChargePowerHeatBeat.setElectricity(data.readShortLE());
                    portChargePowerHeatBeat.setEnvironmentTemperature(data.readByte());
                    portChargePowerHeatBeat.setPortTemperature(data.readByte());
                    if (data.readableBytes() >= 4) {
                        portChargePowerHeatBeat.setTimestamp(data.readIntLE());
                    }
                    if (data.readableBytes() >= 2) {
                        portChargePowerHeatBeat.setTakeTime(data.readShortLE());
                    }
                    log.info("data = [{}]", portChargePowerHeatBeat);
                    break;
                }
                case 0x42: {
                    log.info("data = [{}] {}", data.readByte(), data.readByte());
                    break;
                }
                //订单结束
                case 0x43: {
                    ChargeFinish chargeFinish = new ChargeFinish();
                    chargeFinish.setPileCode(pileCode);
                    chargeFinish.setChargeTime(data.readShortLE());
                    chargeFinish.setMaxPower(data.readShortLE());
                    chargeFinish.setElectric(data.readShortLE());
                    chargeFinish.setPort(data.readByte());
                    chargeFinish.setLunchMode(data.readByte());
                    chargeFinish.setCardId(data.readIntLE());
                    chargeFinish.setStopReason(data.readByte());
                    data.skipBytes(8);
                    chargeFinish.setOrderId(data.readLongLE() + "");
                    log.info("data = [{}]", chargeFinish);
                    break;
                }
                //充电口状态
                case 0x44: {
                    PortStatus portStatus = new PortStatus();
                    portStatus.setPileCode(pileCode);
                    portStatus.setPushType(data.readByte());
                    portStatus.setPort(data.readByte());
                    data.skipBytes(8);
                    portStatus.setOrderId(data.readLongLE() + "");
                    break;
                }

                //0x82= 无符号的130，补码为-126
                case -126: {
                    byte response = data.readByte();
                    data.skipBytes(8);
                    long orderNo = data.readLongLE();
                    byte port = data.readByte();
                    byte waitPort = data.readByte();
                    log.info("0x82 :response:[{}] orderNo:[{}] port:[{}] waitPort:[{}]", response, orderNo, port,
                            waitPort);
                    break;
                }
                default: {
                    log.debug("unknown command [{}]", msg);
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(data);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("{}", ctx);
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught:ctx = {} , cause = {}", ctx, cause);
        Integer pileCode = ctx.channel().attr(pileCodeAttr).get();
        if (pileCode != null) {
            log.info("offline: pileCode:[{}]  channelHandlerContext = [{}]", pileCode, ctx);
        }
        ctx.close();
    }
}
