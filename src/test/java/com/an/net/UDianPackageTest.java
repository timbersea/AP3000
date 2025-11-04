package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class UDianPackageTest {
    private static final Logger log = LoggerFactory.getLogger(UDianPackageTest.class);

    @Test
    public void testBuildFromHexString() {
        UDianPackage uDianPackage = UDianPackage.buildFromHexString(
                "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05");
        log.debug("testBuildFromHexString:{}", uDianPackage);
        Assertions.assertEquals("444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05",
                uDianPackage.toHexString());
    }

    @Test
    public void testEncode() {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        UDianPackage msg = new UDianPackage();
        msg.setDny("DNY");
        msg.setLength((short) 29);
        msg.setPhysicalId(78329659);
        msg.setMessageId((short) 185);
        msg.setCommand((byte) 1);
        msg.setData(ByteBufUtil.decodeHexDump("7E008C080200030000E40000003B022907022000"));
        msg.setCheck((short) 1389);

        out.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        out.writeShortLE(msg.getLength());
        out.writeIntLE(msg.getPhysicalId());
        out.writeShortLE(msg.getMessageId());
        out.writeByte(msg.getCommand());
        out.writeBytes(msg.getData());
        out.writeShortLE(msg.getCheck());
        byte[] bytes = new byte[out.readableBytes()];
        out.readBytes(bytes);

        Assertions.assertEquals("444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05",
                DatatypeConverter.printHexBinary(bytes));

    }

    @Test
    public void testGenerateMessageId() {
        for (int i = 0; i < 200; i++) {
            log.info("testGenerateMessageId:[{}]", UDianPackage.generateMessageId());
        }
    }
}