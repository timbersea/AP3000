package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class AP3000CodecTest {
    EmbeddedChannel channel;
    @Before
    public void before(){
        //建立连接后发送simCardNo
        String hexString = "3839383630343438313631383730303634383135";
         channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(DatatypeConverter.parseHexBinary(hexString));
        //验证写数据返回True
        channel.writeInbound(out);
        channel.flush();
        channel.readInbound();
    }

    @Test
    public void testEncode() {
        String hexString = "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05";
        EmbeddedChannel channel = new EmbeddedChannel(new AP3000Codec(), new LoggingHandler(LogLevel.DEBUG));

        UDianPackage uDianPackage = UDianPackage.buildFromHexString(hexString);

        //模拟写出数据
        org.junit.Assert.assertTrue(channel.writeOutbound(uDianPackage));
        channel.flush();
        org.junit.Assert.assertTrue(channel.finish());
        ByteBuf o = channel.readOutbound();
        byte[] bytes = new byte[o.readableBytes()];
        o.readBytes(bytes);
        Assert.assertEquals(hexString, DatatypeConverter.printHexBinary(bytes));
    }

    @Test
    public void testDecode() {
        String hexString = "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05";
        EmbeddedChannel channel = new EmbeddedChannel(new AP3000Codec());

        UDianPackage msg = UDianPackage.buildFromHexString(hexString);
        ByteBuf out = Unpooled.buffer();

        out.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        channel.flush();
        channel.read();

        out.writeShortLE(msg.getLength());
        channel.flush();
        channel.read();


        out.writeIntLE(msg.getPhysicalId());
        channel.flush();
        channel.read();


        out.writeShortLE(msg.getMessageId());
        channel.flush();
        channel.read();


        out.writeByte(msg.getCommand());
        channel.flush();
        channel.read();


        out.writeBytes(msg.getData());
        channel.flush();
        channel.read();


        out.writeShortLE(msg.getCheck());
        channel.flush();
        channel.read();


        //验证写数据返回True
        channel.writeInbound(out);
        channel.flush();
        channel.read();

        channel.finish();
        UDianPackage o = channel.readInbound();
    }

    @Test
    public void testGetYouDianPackage() {
    }

    /**
     * 设备心跳包
     */
    @Test
    public void test0() {

        String hexString = "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
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

    /**
     * 设备注册
     */
    @Test
    public void test1(){
        String hexString = "444E5913003B37AB04B900207E00021421000000E4009104";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
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
    /**
     * 设备获取服务器时间
     */
    @Test
    public void test2(){
        String hexString = "444E590D003B37AB04B90022090EA95F1304";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
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

    /**
     * 设备心跳包（21指令）
     */
    @Test
    public void test3(){
        String hexString = "444E5910003B37AB0401002198080200000905EE02";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
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
    /**
     * 刷卡
     */
    @Test
    public void test4(){
        String hexString = "444E5911003B37AB040100027A8D05DD000100000A04";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
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

    /**
     * 消费结算
     */
    @Test
    public void test5(){
        String hexString = "444E5928003B37AB04010003100EE80330000101000000000120190901180000130030380102030405E8034405";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
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


    @Test
    public void test6(){
        //建立连接后发送simCardNo
        String hexString = "3839383630343438313631383730303634383135";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(DatatypeConverter.parseHexBinary(hexString));
        //验证写数据返回True
        channel.writeInbound(out);
        channel.flush();
        channel.readInbound();


        //设备心跳包
        String heartBeatHexString = "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05";

        UDianPackage msg = UDianPackage.buildFromHexString(heartBeatHexString);
        ByteBuf out1 = Unpooled.buffer();
        out1.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        out1.writeShortLE(msg.getLength());
        out1.writeIntLE(msg.getPhysicalId());
        out1.writeShortLE(msg.getMessageId());
        out1.writeByte(msg.getCommand());
        out1.writeBytes(msg.getData());
        out1.writeShortLE(msg.getCheck());
        //验证写数据返回True
        channel.writeInbound(out1);
        channel.flush();
        channel.readInbound();

        //发送一个链路保活的link数据
        ByteBuf link = Unpooled.buffer();
        link.writeBytes("link".getBytes(StandardCharsets.UTF_8));
        channel.writeInbound(link);
        channel.flush();
        channel.readInbound();


        //获取服务器时间
        String swipingCard = "444E590D003B37AB04B90022090EA95F1304";

        UDianPackage uDianSwipingCard = UDianPackage.buildFromHexString(swipingCard);
        ByteBuf swipingCardBuf = Unpooled.buffer();
        swipingCardBuf.writeBytes(uDianSwipingCard.getDny().getBytes(StandardCharsets.UTF_8));
        swipingCardBuf.writeShortLE(uDianSwipingCard.getLength());
        swipingCardBuf.writeIntLE(uDianSwipingCard.getPhysicalId());
        swipingCardBuf.writeShortLE(uDianSwipingCard.getMessageId());
        swipingCardBuf.writeByte(uDianSwipingCard.getCommand());
        swipingCardBuf.writeBytes(uDianSwipingCard.getData());
        swipingCardBuf.writeShortLE(uDianSwipingCard.getCheck());
        //验证写数据返回True
        channel.writeInbound(swipingCardBuf);
        channel.flush();
        channel.readInbound();
    }

    @Test
    public void test7(){
        //建立连接后发送simCardNo
        String hexString = "3839383630343438313631383730303634383135";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(DatatypeConverter.parseHexBinary(hexString));
        //验证写数据返回True
        channel.writeInbound(out);
        channel.flush();
        channel.readInbound();


        //设备心跳包
        String heartBeatHexString = "444E591D003B37AB04B900017E008C080200030000E40000003B0229070220006D05";

        UDianPackage msg = UDianPackage.buildFromHexString(heartBeatHexString);
        ByteBuf out1 = Unpooled.buffer();
        out1.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        out1.writeShortLE(msg.getLength());
        out1.writeIntLE(msg.getPhysicalId());
        out1.writeShortLE(msg.getMessageId());
        out1.writeByte(msg.getCommand());
        out1.writeBytes(msg.getData());
        out1.writeShortLE(msg.getCheck());
        //验证写数据返回True
        channel.writeInbound(out1);
        channel.flush();
        channel.readInbound();

        //发送一个链路保活的link数据
        ByteBuf link = Unpooled.buffer();
        link.writeBytes("link".getBytes(StandardCharsets.UTF_8));
        channel.writeInbound(link);
        channel.flush();
        channel.readInbound();


        //获取服务器时间
        String swipingCard = "444E590D003B37AB04B90022090EA95F1304";

        UDianPackage uDianSwipingCard = UDianPackage.buildFromHexString(swipingCard);
        ByteBuf swipingCardBuf = Unpooled.buffer();
        swipingCardBuf.writeBytes(uDianSwipingCard.getDny().getBytes(StandardCharsets.UTF_8));
        swipingCardBuf.writeShortLE(uDianSwipingCard.getLength());
        swipingCardBuf.writeIntLE(uDianSwipingCard.getPhysicalId());
        swipingCardBuf.writeShortLE(uDianSwipingCard.getMessageId());
        swipingCardBuf.writeByte(uDianSwipingCard.getCommand());
        swipingCardBuf.writeBytes(uDianSwipingCard.getData());
        swipingCardBuf.writeShortLE(uDianSwipingCard.getCheck());
        //验证写数据返回True
        channel.writeInbound(swipingCardBuf);
        channel.flush();
        channel.readInbound();
    }

    @Test
    public void test8(){
        String hexString = "444E5932003B37AB040A00060101100E300001E803B0042003E803201909011800001300303801020304050100E8039808C7015500DA08";
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new AP3000Codec(),
                new MessageHandler());
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