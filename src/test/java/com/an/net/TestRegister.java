package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class TestRegister {
    EmbeddedChannel channel;

    @BeforeEach
    public void before() {
        //建立连接后发送simCardNo
        String hexString = "3839383630343438313631383730303634383135";
        channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG), new AP3000Codec(),
                new MessageHandler());
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(DatatypeConverter.parseHexBinary(hexString));
        //验证写数据返回True
        channel.writeInbound(out);
        channel.flush();
        channel.readInbound();
    }

    @Test
    public void test8() {
        //注册
        String hexString = "444e590f0057a4d804a000207e02021931066304";
        UDianPackage msg = UDianPackage.buildFromHexString(hexString);
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        out.writeShortLE(msg.getLength());
        out.writeIntLE(msg.getPhysicalId());
        out.writeShortLE(msg.getMessageId());
        out.writeByte(msg.getCommand());
        out.writeBytes(msg.getData());
        out.writeShortLE(msg.getCheck());
        //验证写数据返回True
        channel.writeInbound(out);
        channel.flush();
        channel.readInbound();
    }

}
