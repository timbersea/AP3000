package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
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

    public static void completeResponse(Short messageId, UDianPackage uDianPackage) {
        CompletableFuture<UDianPackage> uDianPackageCompletableFuture = completableFutureMap.get(messageId);
        if (uDianPackageCompletableFuture != null) {
            log.debug("completeResponse:pileCode:[{}] messageId = [{}], uDianPackage = [{}]",
                    uDianPackage.getPileCode(), messageId,
                    uDianPackage);
            uDianPackageCompletableFuture.complete(uDianPackage);
        }
    }

    public static void asyncWriteData(Integer pileCode, UDianPackage uDianPackage) throws TException {
        ChannelHandlerContext channelHandlerContext = pileCodeChannelContext.get(pileCode);
        if (channelHandlerContext == null || !channelHandlerContext.channel().isActive()) {
            throw new TApplicationException(pileCode + " is not connect to server");
        }
        Byte b = channelHandlerContext.attr(GlobalContext.deviceTypeAttr).get();
        if(uDianPackage.getPhysicalId()==null){
            ByteBuf buffer = Unpooled.buffer(4);
            buffer.writeByte(pileCode&0xFF);
            buffer.writeByte((pileCode&0xFF00)>>8);
            buffer.writeByte((pileCode&0xFF0000)>>16);
            buffer.writeByte(b);
            int physicalId = buffer.readIntLE();
            ReferenceCountUtil.release(buffer);
            uDianPackage.setPhysicalId(physicalId);
        }
        channelHandlerContext.writeAndFlush(uDianPackage);
    }

    public static UDianPackage requestAndResponse(Integer pileCode, UDianPackage uDianPackage) throws TException {
        asyncWriteData(pileCode, uDianPackage);
        CompletableFuture<UDianPackage> uDianPackageCompletableFuture = new CompletableFuture<>();
        completableFutureMap.put(uDianPackage.getMessageId(), uDianPackageCompletableFuture);
        try {
            log.debug("request to pileCode: [{}] messageId [{}]  wait for response", pileCode,
                    uDianPackage.getMessageId());
            return uDianPackageCompletableFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.interrupted();
            throw new TApplicationException(e.getMessage());
        } catch (ExecutionException e) {
            throw new TApplicationException(e.getMessage());
        } catch (TimeoutException e) {
            throw new TApplicationException(pileCode + " response timeout for 3 seconds");
        } finally {
            completableFutureMap.remove(uDianPackage.getMessageId());
        }
    }
}
