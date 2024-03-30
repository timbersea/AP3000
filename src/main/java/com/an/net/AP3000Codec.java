package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AP3000Codec extends ByteToMessageCodec<UDianPackage> {
    private static final Logger log = LoggerFactory.getLogger(AP3000Codec.class);

    private static final AttributeKey<String> simAttr = AttributeKey.newInstance("simNo");


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UDianPackage msg, ByteBuf out) throws Exception {
        log.debug("send to pileCode:[{}] msg:[{}]", channelHandlerContext.channel().attr(GlobalContext.pileCodeAttr), msg);
        out.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        if (msg.getLength() > 256) {
            throw new TooLongFrameException("length must less than 256 " + msg.toHexString());
        }
        out.writeShortLE(msg.getLength());
        out.writeIntLE(msg.getPhysicalId());
        out.writeShortLE(msg.getMessageId());
        out.writeByte(msg.getCommand());
        out.writeBytes(msg.getData());
        out.writeShortLE(calCheck(msg));
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //这是通信模块每次连上socket时，都会（第一时间）发送一次sim卡号给socket
        if (channelHandlerContext.channel().attr(simAttr).get() == null) {
            if (byteBuf.readableBytes() >= 20) {
                ByteBuf simNoBuf = byteBuf.slice(0, 20);
                byte[] simNoBytes = new byte[20];
                simNoBuf.readBytes(simNoBytes, 0, 20);
                String simNo = DatatypeConverter.printHexBinary(simNoBytes);
                if ("38393836".equals(simNo.substring(0, 8))) {
                    log.info("decode:channel = [{}], simNo = [{}]", channelHandlerContext.channel(), simNo);
                    channelHandlerContext.channel().attr(simAttr).setIfAbsent(simNo);
                    byteBuf.skipBytes(20);
                }
            }
        } else {
            //{6C 69 6E 6B }link是模块心跳包，是防中国移动踢掉网的，长度固定为4字节，（服务器无需应答）。
            if (byteBuf.readableBytes() >= 4) {
                ByteBuf linkBuf = byteBuf.slice(0, 4);
                byte[] linkByte = new byte[4];
                linkBuf.readBytes(linkByte);
                String link = DatatypeConverter.printHexBinary(linkByte);
                if ("6C696E6B".equals(link)) {
                    if (log.isDebugEnabled()) {
                        log.debug("pileCode = [{}],read link [{}]",
                                channelHandlerContext.channel().attr(GlobalContext.pileCodeAttr), link);
                    }
                    byteBuf.skipBytes(4);
                }
            }
            ByteBuf decoded = decode(byteBuf);
            if (decoded != null) {
                UDianPackage uDianPackage = getYouDianPackage(decoded);
                list.add(uDianPackage);
            }
        }
    }

    public static UDianPackage getYouDianPackage(ByteBuf decoded) {
        ByteBuf data = null;
        try {
            byte[] toCalCheck = new byte[decoded.readableBytes() - 2];//去掉最后两字节的检校值后的数据参与计算校验值
            decoded.getBytes(0, toCalCheck, 0, toCalCheck.length);
            decoded.skipBytes(3);
            int length = decoded.readUnsignedShortLE();
            int physicalId = decoded.readIntLE();
            int messageId = decoded.readUnsignedShortLE();
            int command = decoded.readByte();
            data = decoded.readBytes(length - 4 - 2 - 1 - 2);
            data.retain();
            int check = decoded.readUnsignedShortLE();

            UDianPackage uDianPackage = new UDianPackage();
            uDianPackage.setDny("DNY");
            uDianPackage.setLength((short) length);
            uDianPackage.setPhysicalId(physicalId);
            uDianPackage.setMessageId((short) messageId);
            uDianPackage.setCommand(command);
            byte[] bytes = new byte[length - 4 - 2 - 1 - 2];
            data.readBytes(bytes);
            uDianPackage.setData(bytes);
            uDianPackage.setCheck((short) check);

            int calCheckValue = calCheck(toCalCheck);
            if (calCheckValue != check) {
                log.debug("cal check value :[{}],receive chekcValue[{}]", calCheckValue, check);
                throw new IllegalArgumentException("calCheckValue: " + calCheckValue + " not equals to check: " + check);
            }
            return uDianPackage;
        } catch (Exception e) {
            throw e;
        } finally {
            ReferenceCountUtil.release(decoded);
            if (data != null) {
                ReferenceCountUtil.release(data);
            }
        }
    }

    private static int calCheck(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum += (b & 0x000000FF);
        }
        return sum;
    }

    public static int calCheck(UDianPackage uDianPackage) {
        ByteBuf out = Unpooled.buffer();
        out.writeBytes(uDianPackage.getDny().getBytes(StandardCharsets.UTF_8));
        out.writeShortLE(uDianPackage.getLength());
        out.writeIntLE(uDianPackage.getPhysicalId());
        out.writeShortLE(uDianPackage.getMessageId());
        out.writeByte(uDianPackage.getCommand());
        out.writeBytes(uDianPackage.getData());
        byte[] toCalCheck = new byte[out.readableBytes()];
        out.readBytes(toCalCheck);
        return calCheck(toCalCheck);
    }

    private ByteBuf decode(ByteBuf in) throws Exception {
        in.markReaderIndex();
        if (in.readableBytes() < 12) {
            return null;
        } else if (in.readableBytes() > 256) {
            ReferenceCountUtil.release(in);
            throw new TooLongFrameException();
        } else {
            ByteBuf byteBuf = in.slice(0, 12);
            byteBuf.retain();
            byteBuf.skipBytes(3);
            int length = byteBuf.readUnsignedShortLE();
            ReferenceCountUtil.release(byteBuf);
            if (in.readableBytes() < (length + 3)) {
                in.resetReaderIndex();
                return null;
            }
            return in.readBytes(length + 3 + 2);
        }
    }
}
