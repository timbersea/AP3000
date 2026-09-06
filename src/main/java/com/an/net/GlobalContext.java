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
    public static final AttributeKey<Integer> pileCodeAttr = AttributeKey.valueOf("pileCode");
    public static final AttributeKey<Byte> deviceTypeAttr = AttributeKey.valueOf("deviceType");
    public static final AttributeKey<Long> activeTimestamp = AttributeKey.valueOf("activeTimestamp");
    private static final Logger log = LoggerFactory.getLogger(GlobalContext.class);
    private static final Map<Integer, ChannelHandlerContext> pileCodeChannelContext = new ConcurrentHashMap<>(1024);
    private static final Map<Long, PendingRequest> completableFutureMap = new ConcurrentHashMap<>(1024);

    static long pendingKey(int pileCode, short messageId) {
        return ((long) pileCode << 16) | (messageId & 0xFFFFL);
    }

    /**
     * 仅当存在匹配的 (pileCode, messageId, command) 等待项时完成。
     */
    static void completeResponse(UDianPackage uDianPackage) {
        long key = pendingKey(uDianPackage.getPileCode(), uDianPackage.getMessageId());
        PendingRequest pending = completableFutureMap.get(key);
        if (pending == null) {
            return;
        }
        if (pending.expectedCommand() != uDianPackage.getCommand()) {
            return;
        }
        log.info("completeResponse:pileCode:[{}] messageId = [{}], uDianPackage = [{}]",
                uDianPackage.getPileCode(), uDianPackage.getMessageId(), uDianPackage);
        pending.future().complete(uDianPackage);
    }

    public static void online(Integer pileCode, ChannelHandlerContext context) {
        pileCodeChannelContext.compute(pileCode, (k, existing) -> {
            if (existing == null || existing == context) {
                if (existing == null) {
                    log.info("pileCode = [{}] online, context = [{}]", pileCode, context);
                }
                return context;
            }
            Long newTs = context.channel().attr(activeTimestamp).get();
            Long oldTs = existing.channel().attr(activeTimestamp).get();
            if (newTs != null && oldTs != null && newTs > oldTs) {
                log.warn("pileCode = [{}] connect {} but last not disconnected , context = [{}]", pileCode,
                        context, existing);
                existing.close();
                return context;
            }
            return existing;
        });
    }

    public static void offline(ChannelHandlerContext channelHandlerContext) {
        Integer pileCode = channelHandlerContext.channel().attr(pileCodeAttr).get();
        if (pileCode != null && pileCodeChannelContext.remove(pileCode, channelHandlerContext)) {
            log.info("offline:channelHandlerContext = [{}]", channelHandlerContext);
        }
    }

    public static UDianPackage requestAndResponse(UDianPackage uDianPackage) {
        int routePileCode = uDianPackage.getOutboundPileCode();
        long key = pendingKey(routePileCode, uDianPackage.getMessageId());
        CompletableFuture<UDianPackage> future = new CompletableFuture<>();
        completableFutureMap.put(key, new PendingRequest(future, uDianPackage.getCommand()));
        try {
            asyncWriteData(uDianPackage);
            log.info("request to pileCode: [{}] messageId [{}]  wait for response", uDianPackage.getPileCode(),
                    uDianPackage.getMessageId());
            return future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e.getMessage());
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getMessage());
        } catch (TimeoutException e) {
            throw new RuntimeException(uDianPackage.getPileCode() + " response timeout for 5 seconds");
        } finally {
            completableFutureMap.remove(key);
        }
    }

    public static void asyncWriteData(UDianPackage uDianPackage) {
        int routePileCode = uDianPackage.getOutboundPileCode();
        ChannelHandlerContext channelHandlerContext = pileCodeChannelContext.get(routePileCode);
        if (channelHandlerContext == null || !channelHandlerContext.channel().isActive()) {
            throw new RuntimeException(routePileCode + " is not connect to server");
        }
        if (!uDianPackage.hasPhysicalId()) {
            Byte deviceType = channelHandlerContext.channel().attr(GlobalContext.deviceTypeAttr).get();
            if (deviceType == null) {
                throw new RuntimeException(routePileCode + " deviceType unknown, wait for inbound register/heartbeat");
            }
            uDianPackage.setPhysicalId(pileCode2PhysicalId(routePileCode, deviceType));
        }
        channelHandlerContext.writeAndFlush(uDianPackage);
    }

    private record PendingRequest(CompletableFuture<UDianPackage> future, int expectedCommand) {
    }
}
