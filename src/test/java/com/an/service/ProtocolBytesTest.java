package com.an.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProtocolBytesTest {

    @Test
    void arrayReturnsCapacityNotWritableLength() {
        ByteBuf buf = Unpooled.buffer(4);
        buf.writeByte(0x11);
        buf.writeByte(0x22);
        // 复现 C7：array() 长度是 capacity，不是 writerIndex
        assertNotEquals(2, buf.array().length);
        assertEquals(2, ByteBufUtil.getBytes(buf).length);
        assertArrayEquals(new byte[]{0x11, 0x22}, ByteBufUtil.getBytes(buf));
        buf.release();
    }

    @Test
    void emptyAllocatedBytesWithoutCopyStayZero() {
        ByteBuf toWrite = Unpooled.buffer();
        toWrite.writeShortLE(0x1234);
        toWrite.writeByte(0x05);
        byte[] bytes = new byte[toWrite.readableBytes()];
        // 复现 C6：未 readBytes 时请求体全 0
        assertArrayEquals(new byte[]{0, 0, 0}, bytes);
        toWrite.readBytes(bytes);
        assertArrayEquals(new byte[]{0x34, 0x12, 0x05}, bytes);
        toWrite.release();
    }

    @Test
    void query90EmptyBufferCannotReadResponseFields() {
        byte[] responseData = new byte[]{0x01, 0x00, 0x02, 0x00, 0x03, 0x04, 0x00, 0x05, 0x00, 0x06, 0x00};
        ByteBuf wrong = Unpooled.buffer(responseData.length);
        assertThrows(IndexOutOfBoundsException.class, wrong::readShortLE);

        ByteBuf right = Unpooled.copiedBuffer(responseData);
        assertEquals(1, right.readShortLE());
        wrong.release();
        right.release();
    }
}
