package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AP3000CodecHardeningTest {

    @Test
    void decodeWorksWithoutSimPrefix() {
        String hex = "444E590A003B37AB04B9002000EF02";
        EmbeddedChannel channel = new EmbeddedChannel(new AP3000Codec());
        ByteBuf in = Unpooled.copiedBuffer(ByteBufUtil.decodeHexDump(hex));
        assertTrue(channel.writeInbound(in));
        UDianPackage msg = channel.readInbound();
        assertNotNull(msg);
        assertEquals(0x20, msg.getCommand() & 0xFF);
        channel.finishAndReleaseAll();
    }

    @Test
    void readFrameRejectsLengthAboveMax() {
        EmbeddedChannel channel = new EmbeddedChannel(new AP3000Codec());
        ByteBuf frame = Unpooled.buffer();
        frame.writeBytes("DNY".getBytes(StandardCharsets.UTF_8));
        frame.writeShortLE(300); // > MAX_LENGTH
        frame.writeIntLE(0x043B373B);
        frame.writeShortLE(1);
        frame.writeByte(0x01);
        frame.writeBytes(new byte[300 - 9]);
        frame.writeShortLE(0);
        channel.writeInbound(frame);
        assertNull(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    @Test
    void calCheckTruncatesTo16Bits() {
        byte[] data = new byte[300];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) 0xFF;
        }
        int sum = 0;
        for (byte b : data) {
            sum += (b & 0xFF);
        }
        assertTrue(sum > 0xFFFF);
        assertEquals(sum & 0xFFFF, AP3000Codec.calCheck(data));
    }

    @Test
    void skipsGarbageUntilDnyMagic() {
        String hex = "444E590A003B37AB04B9002000EF02";
        EmbeddedChannel channel = new EmbeddedChannel(new AP3000Codec());
        ByteBuf in = Unpooled.buffer();
        in.writeByte(0x00);
        in.writeByte(0xFF);
        in.writeBytes(ByteBufUtil.decodeHexDump(hex));
        assertTrue(channel.writeInbound(in));
        assertNotNull(channel.readInbound());
        channel.finishAndReleaseAll();
    }

    @Test
    void buildFromHexStringDoesNotLeak() {
        String hex = "444E590A003B37AB04B9002000EF02";
        UDianPackage pkg = UDianPackage.buildFromHexString(hex);
        assertEquals(0x20, pkg.getCommand() & 0xFF);
    }
}
