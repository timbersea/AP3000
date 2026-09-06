package com.an.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalContextTest {

    @Test
    void asyncWriteData_usesOutboundPileCodeAndFillsPhysicalIdFromInboundSession() {
        int pileCode = 14100694;
        byte deviceType = 0x04;
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        ChannelHandlerContext ctx = channel.pipeline().firstContext();
        channel.attr(GlobalContext.activeTimestamp).set(System.currentTimeMillis());
        channel.attr(GlobalContext.pileCodeAttr).set(pileCode);
        channel.attr(GlobalContext.deviceTypeAttr).set(deviceType);
        GlobalContext.online(pileCode, ctx);

        UDianPackage outbound = new UDianPackage(pileCode, (byte) 0x87, new byte[0]);
        assertEquals(0, outbound.getPileCode(), "协议 pileCode 在补全 physicalId 前只能为 0");

        GlobalContext.asyncWriteData(outbound);

        int expectedPhysicalId = UDianPackage.pileCode2PhysicalId(pileCode, deviceType);
        assertEquals(expectedPhysicalId, outbound.getPhysicalId().intValue());
        assertEquals(pileCode, outbound.getPileCode(), "补全后 getPileCode 仍只从 physicalId 推导");
        assertNotNull(channel.readOutbound());

        GlobalContext.offline(ctx);
        channel.finishAndReleaseAll();
    }

    @Test
    void offline_doesNotRemoveNewerConnection() {
        int pileCode = 10001;
        EmbeddedChannel oldCh = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        EmbeddedChannel newCh = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        ChannelHandlerContext oldCtx = oldCh.pipeline().firstContext();
        ChannelHandlerContext newCtx = newCh.pipeline().firstContext();

        oldCh.attr(GlobalContext.activeTimestamp).set(1L);
        oldCh.attr(GlobalContext.pileCodeAttr).set(pileCode);
        newCh.attr(GlobalContext.activeTimestamp).set(2L);
        newCh.attr(GlobalContext.pileCodeAttr).set(pileCode);
        newCh.attr(GlobalContext.deviceTypeAttr).set((byte) 1);

        GlobalContext.online(pileCode, oldCtx);
        GlobalContext.online(pileCode, newCtx);
        GlobalContext.offline(oldCtx);

        UDianPackage outbound = new UDianPackage(pileCode, (byte) 0x87, new byte[0]);
        GlobalContext.asyncWriteData(outbound);
        assertNotNull(newCh.readOutbound());

        GlobalContext.offline(newCtx);
        oldCh.finishAndReleaseAll();
        newCh.finishAndReleaseAll();
    }

    @Test
    void requestAndResponse_registersFutureBeforeWriteSoFastReplyCompletes() {
        int pileCode = 20002;
        ChannelHandlerContext ctx = org.mockito.Mockito.mock(ChannelHandlerContext.class);
        io.netty.channel.Channel channel = org.mockito.Mockito.mock(io.netty.channel.Channel.class,
                org.mockito.Mockito.RETURNS_DEEP_STUBS);
        org.mockito.Mockito.when(ctx.channel()).thenReturn(channel);
        org.mockito.Mockito.when(channel.isActive()).thenReturn(true);

        io.netty.util.Attribute<Long> tsAttr = org.mockito.Mockito.mock(io.netty.util.Attribute.class);
        org.mockito.Mockito.when(channel.attr(GlobalContext.activeTimestamp)).thenReturn(tsAttr);
        org.mockito.Mockito.when(tsAttr.get()).thenReturn(System.currentTimeMillis());

        io.netty.util.Attribute<Integer> pileAttr = org.mockito.Mockito.mock(io.netty.util.Attribute.class);
        org.mockito.Mockito.when(channel.attr(GlobalContext.pileCodeAttr)).thenReturn(pileAttr);
        org.mockito.Mockito.when(pileAttr.get()).thenReturn(pileCode);

        io.netty.util.Attribute<Byte> typeAttr = org.mockito.Mockito.mock(io.netty.util.Attribute.class);
        org.mockito.Mockito.when(channel.attr(GlobalContext.deviceTypeAttr)).thenReturn(typeAttr);
        org.mockito.Mockito.when(typeAttr.get()).thenReturn((byte) 2);

        org.mockito.Mockito.when(ctx.writeAndFlush(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
            UDianPackage req = invocation.getArgument(0);
            // 若 Future 尚未 put，complete 会被丢弃 → 随后 get 超时
            GlobalContext.completeResponse(req.getReply(new byte[]{1}));
            return org.mockito.Mockito.mock(io.netty.channel.ChannelFuture.class);
        });

        GlobalContext.online(pileCode, ctx);

        UDianPackage request = new UDianPackage(pileCode, (byte) 0x87, new byte[0]);
        UDianPackage response = GlobalContext.requestAndResponse(request);
        assertEquals(1, response.getData()[0]);

        GlobalContext.offline(ctx);
    }

    @Test
    void completeResponse_requiresMatchingPileAndCommand() throws Exception {
        int pileCode = 40004;
        ChannelHandlerContext ctx = org.mockito.Mockito.mock(ChannelHandlerContext.class);
        io.netty.channel.Channel channel = org.mockito.Mockito.mock(io.netty.channel.Channel.class,
                org.mockito.Mockito.RETURNS_DEEP_STUBS);
        org.mockito.Mockito.when(ctx.channel()).thenReturn(channel);
        org.mockito.Mockito.when(channel.isActive()).thenReturn(true);

        io.netty.util.Attribute<Long> tsAttr = org.mockito.Mockito.mock(io.netty.util.Attribute.class);
        org.mockito.Mockito.when(channel.attr(GlobalContext.activeTimestamp)).thenReturn(tsAttr);
        org.mockito.Mockito.when(tsAttr.get()).thenReturn(System.currentTimeMillis());

        io.netty.util.Attribute<Integer> pileAttr = org.mockito.Mockito.mock(io.netty.util.Attribute.class);
        org.mockito.Mockito.when(channel.attr(GlobalContext.pileCodeAttr)).thenReturn(pileAttr);
        org.mockito.Mockito.when(pileAttr.get()).thenReturn(pileCode);

        io.netty.util.Attribute<Byte> typeAttr = org.mockito.Mockito.mock(io.netty.util.Attribute.class);
        org.mockito.Mockito.when(channel.attr(GlobalContext.deviceTypeAttr)).thenReturn(typeAttr);
        org.mockito.Mockito.when(typeAttr.get()).thenReturn((byte) 2);

        java.util.concurrent.atomic.AtomicReference<UDianPackage> sent =
                new java.util.concurrent.atomic.AtomicReference<>();
        org.mockito.Mockito.when(ctx.writeAndFlush(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
            sent.set(invocation.getArgument(0));
            return org.mockito.Mockito.mock(io.netty.channel.ChannelFuture.class);
        });

        GlobalContext.online(pileCode, ctx);
        UDianPackage request = new UDianPackage(pileCode, (byte) 0x87, new byte[0]);

        java.util.concurrent.CompletableFuture<UDianPackage> async =
                java.util.concurrent.CompletableFuture.supplyAsync(() -> GlobalContext.requestAndResponse(request));

        java.util.concurrent.TimeUnit.MILLISECONDS.sleep(200);
        UDianPackage outbound = sent.get();
        assertNotNull(outbound);

        UDianPackage heartbeat = outbound.getReply(new byte[]{0});
        heartbeat.setCommand(0x01);
        GlobalContext.completeResponse(heartbeat);
        assertFalse(async.isDone());

        GlobalContext.completeResponse(outbound.getReply(new byte[]{9}));
        assertEquals(9, async.get(2, java.util.concurrent.TimeUnit.SECONDS).getData()[0]);
        GlobalContext.offline(ctx);
    }

    @Test
    void online_closesOlderChannelWhenReplaced() {
        int pileCode = 30003;
        EmbeddedChannel oldCh = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        EmbeddedChannel newCh = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        ChannelHandlerContext oldCtx = oldCh.pipeline().firstContext();
        ChannelHandlerContext newCtx = newCh.pipeline().firstContext();

        oldCh.attr(GlobalContext.activeTimestamp).set(1L);
        oldCh.attr(GlobalContext.pileCodeAttr).set(pileCode);
        newCh.attr(GlobalContext.activeTimestamp).set(2L);
        newCh.attr(GlobalContext.pileCodeAttr).set(pileCode);

        GlobalContext.online(pileCode, oldCtx);
        GlobalContext.online(pileCode, newCtx);

        assertFalse(oldCh.isActive());
        assertTrue(newCh.isActive());

        GlobalContext.offline(newCtx);
        newCh.finishAndReleaseAll();
    }
}
