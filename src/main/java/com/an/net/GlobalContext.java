package com.an.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class GlobalContext {
    private static final Logger log = LoggerFactory.getLogger(GlobalContext.class);
    private static final Map<Integer, ChannelHandlerContext> physicalIdChannelContext = new ConcurrentHashMap<>(1024);
    private static final Map<Short, CompletableFuture<UDianPackage>> completableFutureMap = new ConcurrentHashMap<>(1024);

    public static final AttributeKey<Integer> physicalIdAttr = AttributeKey.newInstance("physicalId");
    public static final AttributeKey<Long> activeTimestamp = AttributeKey.newInstance("activeTimestamp");

    public static void online(Integer physicalId, ChannelHandlerContext context) {
        physicalIdChannelContext.computeIfPresent(physicalId,
                (k, v) -> {
                    if (context.channel().attr(activeTimestamp).get() > v.channel().attr(activeTimestamp).get()) {
                        log.warn("physicalId = [{}] connect {} but last not disconnected , context = [{}]", physicalId,
                                context, v);
                        return context;
                    } else {
                        return v;
                    }
                });
        ChannelHandlerContext channelHandlerContext = physicalIdChannelContext.putIfAbsent(physicalId, context);
        if (channelHandlerContext == null) {
            log.info("physicalId = [{}] online, context = [{}]", physicalId, context);
        }
    }

    public static void offline(ChannelHandlerContext deviceCode) {
        if(deviceCode.channel().attr(physicalIdAttr).get()!=null){
            physicalIdChannelContext.remove(deviceCode.channel().attr(physicalIdAttr).get());
            log.info("offline:deviceCode = [{}]", deviceCode);
        }
    }

    public static void completeResponse(Short messageId, UDianPackage uDianPackage) {
        CompletableFuture<UDianPackage> uDianPackageCompletableFuture = completableFutureMap.get(messageId);
        if (uDianPackageCompletableFuture != null) {
            log.debug("completeResponse:physicalId:[{}] messageId = [{}], uDianPackage = [{}]",
                    uDianPackage.getPhysicalId(), messageId,
                    uDianPackage);
            uDianPackageCompletableFuture.complete(uDianPackage);
        }
    }

    public static void asyncWriteData(Integer physicalId, UDianPackage uDianPackage) throws TException {
        ChannelHandlerContext channelHandlerContext = physicalIdChannelContext.get(physicalId);
        if (channelHandlerContext == null||!channelHandlerContext.channel().isActive()) {
            throw new TApplicationException(physicalId + " is not connect to server");
        }
        channelHandlerContext.writeAndFlush(uDianPackage);
    }

    public static UDianPackage requestAndResponse(Integer physicalId, UDianPackage uDianPackage) throws TException {
        asyncWriteData(physicalId,uDianPackage);
        CompletableFuture<UDianPackage> uDianPackageCompletableFuture = new CompletableFuture<>();
        completableFutureMap.put(uDianPackage.getMessageId(), uDianPackageCompletableFuture);
        try {
            log.debug("request to physicalId: [{}] messageId [{}]  wait for response", physicalId,
                    uDianPackage.getMessageId());
            return uDianPackageCompletableFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.interrupted();
            throw new TApplicationException(e.getMessage());
        } catch (ExecutionException e) {
            throw new TApplicationException(e.getMessage());
        } catch (TimeoutException e) {
            throw new TApplicationException(physicalId + " response timeout for 3 seconds");
        } finally {
            completableFutureMap.remove(uDianPackage.getMessageId());
        }
    }
}
