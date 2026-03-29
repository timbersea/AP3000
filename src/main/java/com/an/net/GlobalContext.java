package com.an.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.an.net.UDianPackage.pileCode2PhysicalId;

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


    public static UDianPackage requestAndResponse(UDianPackage uDianPackage){
        asyncWriteData(uDianPackage);
        CompletableFuture<UDianPackage> uDianPackageCompletableFuture = new CompletableFuture<>();
        completableFutureMap.put(uDianPackage.getMessageId(), uDianPackageCompletableFuture);
        try {
            log.info("request to pileCode: [{}] messageId [{}]  wait for response", uDianPackage.getPileCode(),
                    uDianPackage.getMessageId());
            return uDianPackageCompletableFuture.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        } catch (TimeoutException e) {
            throw new RuntimeException(uDianPackage.getPileCode() + " response timeout for 3 seconds");
        } finally {
            completableFutureMap.remove(uDianPackage.getMessageId());
        }
    }
}
