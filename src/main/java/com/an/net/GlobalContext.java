package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class GlobalContext {
    private static final Logger log = LoggerFactory.getLogger(GlobalContext.class);
    private static final Map<Integer, ChannelHandlerContext> pileCodeChannelContext = new ConcurrentHashMap<>(1024);
    private static final Map<Short, CompletableFuture<UDianPackage>> completableFutureMap = new ConcurrentHashMap<>(1024);

    public static final AttributeKey<Integer> pileCodeAttr = AttributeKey.newInstance("pileCode");
    public static final AttributeKey<Byte> deviceTypeAttr = AttributeKey.newInstance("deviceType");
    public static final AttributeKey<Long> activeTimestamp = AttributeKey.newInstance("activeTimestamp");

    public static void online(Integer pileCode, ChannelHandlerContext context) {
        pileCodeChannelContext.computeIfPresent(pileCode,
                (k, v) -> {
                    if (context.channel().attr(activeTimestamp).get() > v.channel().attr(activeTimestamp).get()) {
                        log.warn("pileCode = [{}] connect {} but last not disconnected , context = [{}]", pileCode,
                                context, v);
                        return context;
                    } else {
                        return v;
                    }
                });
        ChannelHandlerContext channelHandlerContext = pileCodeChannelContext.putIfAbsent(pileCode, context);
        if (channelHandlerContext == null) {
            log.info("pileCode = [{}] online, context = [{}]", pileCode, context);
        }
    }

    public static void offline(ChannelHandlerContext channelHandlerContext) {
        Integer pileCode = channelHandlerContext.channel().attr(pileCodeAttr).get();
        if (pileCode != null) {
            pileCodeChannelContext.remove(pileCode);
            log.info("offline:channelHandlerContext = [{}]", channelHandlerContext);
        }
    }

    static void completeResponse(Short messageId, UDianPackage uDianPackage) {
        CompletableFuture<UDianPackage> uDianPackageCompletableFuture = completableFutureMap.get(messageId);
        if (uDianPackageCompletableFuture != null) {
            log.info("completeResponse:pileCode:[{}] messageId = [{}], uDianPackage = [{}]",
                    uDianPackage.getPileCode(), messageId,
                    uDianPackage);
            uDianPackageCompletableFuture.complete(uDianPackage);
        }
    }

    public static void asyncWriteData(UDianPackage uDianPackage)  {
        int pileCode = uDianPackage.getPileCode();
        ChannelHandlerContext channelHandlerContext = pileCodeChannelContext.get(pileCode);
        if (channelHandlerContext == null || !channelHandlerContext.channel().isActive()) {
            throw new RuntimeException(pileCode + " is not connect to server");
        }
        Byte deviceType = channelHandlerContext.channel().attr(GlobalContext.deviceTypeAttr).get();
        if(uDianPackage.getPhysicalId()==null){
            uDianPackage.setPhysicalId(pileCode2PhysicalId(pileCode,deviceType));
        }
        channelHandlerContext.writeAndFlush(uDianPackage);
    }

    /**
     * pileCode转physicalId
     * @param pileCode 业务系统使用pileCode标识设备
     * @param deviceTypeAttr 设备型号
     * @return 设备与服务器通信识别的physicalId
     */
    private static int pileCode2PhysicalId(Integer pileCode,byte deviceTypeAttr){
        ByteBuf buffer = Unpooled.buffer(4);
        buffer.writeByte(pileCode&0xFF);
        buffer.writeByte((pileCode&0xFF00)>>8);
        buffer.writeByte((pileCode&0xFF0000)>>16);
        buffer.writeByte(deviceTypeAttr);
        int physicalId = buffer.readIntLE();
        ReferenceCountUtil.release(buffer);
        return physicalId;
    }

    public static UDianPackage requestAndResponse(UDianPackage uDianPackage){
        asyncWriteData(uDianPackage);
        CompletableFuture<UDianPackage> uDianPackageCompletableFuture = new CompletableFuture<>();
        completableFutureMap.put(uDianPackage.getMessageId(), uDianPackageCompletableFuture);
        try {
            log.info("request to pileCode: [{}] messageId [{}]  wait for response", uDianPackage.getPileCode(),
                    uDianPackage.getMessageId());
            return uDianPackageCompletableFuture.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        } catch (TimeoutException e) {
            throw new RuntimeException(uDianPackage.getPileCode() + " response timeout for 3 seconds");
        } finally {
            completableFutureMap.remove(uDianPackage.getMessageId());
        }
    }
}
