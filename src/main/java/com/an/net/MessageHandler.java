package com.an.net;

import com.an.common.ConsumerNet;
import com.an.common.ResponseCode;
import com.an.entity.req.ChargeFinish;
import com.an.entity.req.ChargePortOrderConfirm;
import com.an.entity.req.PortStatus;
import com.an.idl.client.ConsumeServiceClient;
import com.an.idl.consumer.PortChargePowerHeatBeat;
import com.an.idl.consumer.SwipingCardResp;
import com.anju.common.dto.OrderAutoFinishChargeDto;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.an.net.GlobalContext.pileCodeAttr;

@Component
@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<UDianPackage> {
    @Resource
    ConsumerNet consumerNet;
    @Resource

    ConsumeServiceClient consumerServiceClient;
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
            switch (command) {
                case 0x01: {
                    com.an.idl.consumer.HeatBeat heatBeat = new com.an.idl.consumer.HeatBeat();
                    //小端转大端
                    heatBeat.setFirmwareVersion(byteBufData.readShortLE());
                    heatBeat.setVoltage(byteBufData.readShortLE());
                    heatBeat.setPortNum(byteBufData.readByte());
                    byte portNum = heatBeat.getPortNum();
                    byte[] portStatus = new byte[portNum];
                    ArrayList<Short> currentPower = new ArrayList<>(portNum * 2);
                    ArrayList<Short> peakPower = new ArrayList<>(portNum * 2);

                    for (int i = 0; i < portNum; i++) {
                        portStatus[i] = byteBufData.readByte();
                    }
                    for (int i = 0; i < portNum; i++) {
                        currentPower.add(byteBufData.readShortLE());
                    }
                    for (int i = 0; i < portNum; i++) {
                        peakPower.add(byteBufData.readShortLE());
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
                    consumerServiceClient.heatBeat(pileCode, heatBeat);
                    break;
                }
                case 0x20: {
                    com.an.idl.consumer.Register register = new com.an.idl.consumer.Register();
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
                    consumerServiceClient.register(pileCode, register);

                    break;
                }
                case 0x21: {
                    com.an.idl.consumer.HeatBeat21 heatBeat21 = new com.an.idl.consumer.HeatBeat21();
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
                    consumerServiceClient.heatBeat21(pileCode, heatBeat21);
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
                    com.an.idl.consumer.SwipingCard swipingCard = new com.an.idl.consumer.SwipingCard();
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
                    SwipingCardResp swipingCardResp = consumerServiceClient.swipingChard(pileCode, swipingCard);
                    ByteBuf buffer = Unpooled.buffer(11);
                    buffer.writeIntLE(swipingCard.getCardId());
                    buffer.writeByte(swipingCard.getCardType());
                    buffer.writeByte(swipingCardResp.getFeeType());
                    buffer.writeIntLE(swipingCardResp.getBalanceValidateDate());
                    buffer.writeByte(swipingCard.getPort());
                    ctx.writeAndFlush(msg.getReply(buffer.array()));
                    break;
                }
                case 0x03: {
                    com.an.idl.consumer.SettleConsume settleConsume = new com.an.idl.consumer.SettleConsume();
                    settleConsume.setChargeTime(byteBufData.readShortLE());
                    settleConsume.setMaxPower(byteBufData.readShortLE());
                    settleConsume.setElectric(byteBufData.readShortLE());
                    settleConsume.setPort(byteBufData.readByte());
                    settleConsume.setLunchMode(byteBufData.readByte());
                    settleConsume.setCardId(byteBufData.readInt());
                    settleConsume.setStopReason(byteBufData.readByte());
                    byteBufData.skipBytes(8);
                    settleConsume.setOrderId(byteBufData.readLongLE());
                    settleConsume.setSecondMaxPower(byteBufData.readShortLE());
                    if (byteBufData.readableBytes() >= 4) {
                        settleConsume.setTimestamp(byteBufData.readIntLE());
                    }
                    if (byteBufData.readableBytes() >= 2) {
                        settleConsume.setOccupiedTime(byteBufData.readShortLE());
                    }
                    ctx.writeAndFlush(msg.getReply(new byte[]{0}));

                    OrderAutoFinishChargeDto dto = new OrderAutoFinishChargeDto();
                    dto.setOrderNo(settleConsume.getOrderId() + "");
                    dto.setPileCode(msg.getPileCode() + "");
                    dto.setGunCode(settleConsume.getPort() + "");
                    dto.setStartTime(new Date(new Date().getTime() - 1 * 3600 * 1000));
                    dto.setEndTime(new Date());


                    dto.setElectricityStart("0");
                    dto.setElectricityEnd("" + settleConsume.getElectric());
                    // 总电量
                    dto.setTotalElectricity(new BigDecimal(settleConsume.getElectric() + ""));
                    // 计损总电量
                    dto.setLossTotalElectricity(dto.getTotalElectricity());
                    // 消费金额
                    dto.setConsumerAmount(new BigDecimal("3.00"));
                    dto.setStopReason(settleConsume.getStopReason() + "");
                    dto.setStopReasonName(ResponseCode.getStopReasonDescription(settleConsume.getStopReason()));

//                String key = CacheConstants.PILE_ORDER_SETTLE_DATA + orderNo;
//                redisCache.setCacheObject(key, dto, 7, TimeUnit.DAYS);
//
//                log.info("ykc1.6订单结算:{}", orderNo);
//                // 通知消费端，订单已结束
                    //   consumerNet.finishOrder(dto);
                    log.info("data = [{}]", settleConsume);
                    ctx.writeAndFlush(msg.getReply(new byte[]{0}));
                    consumerServiceClient.settleConsume(pileCode, settleConsume);
                    break;
                }
                case 0x04: {
                    ChargePortOrderConfirm chargePortOrderConfirm = new ChargePortOrderConfirm();
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
                case 0x06: {
                    PortChargePowerHeatBeat portChargePowerHeatBeat = new PortChargePowerHeatBeat();
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
                    portChargePowerHeatBeat.setOrderId(byteBufData.readLongLE());
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
                    consumerServiceClient.portChargePowerHeatBeat(pileCode, portChargePowerHeatBeat);
                    break;
                }
                case 0x42: {
                    log.info("data = [{}] {}", byteBufData.readByte(), byteBufData.readByte());
                    break;
                }
                case 0x43: {
                    ChargeFinish chargeFinish = new ChargeFinish();
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
                case 0x44: {
                    PortStatus portStatus = new PortStatus();
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
