package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AP3000Codec extends ByteToMessageCodec<UDianPackage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UDianPackage msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        out.writeShortLE(msg.getLength());
        out.writeIntLE(msg.getPhysicalId());
        out.writeShortLE(msg.getMessageId());
        out.writeByte(msg.getCommand());
        out.writeBytes(msg.getData());
        out.writeShortLE(msg.getCheck());
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        ByteBuf decoded = decode(byteBuf);
        if (decoded != null) {
            UDianPackage uDianPackage = getYouDianPackage(decoded);
            list.add(uDianPackage);
        }
    }

    public static UDianPackage getYouDianPackage(ByteBuf decoded) {
        decoded.readBytes(3);
        int length = decoded.readUnsignedShortLE();
        int physicalId = decoded.readIntLE();
        int messageId = decoded.readUnsignedShortLE();
        byte command = decoded.readByte();
        ByteBuf data = decoded.readBytes(length - 4 - 2 - 1 - 2);
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

        ReferenceCountUtil.release(decoded);
        return uDianPackage;
    }

    private ByteBuf decode(ByteBuf in) throws Exception {
        in.markReaderIndex();
        if (in.readableBytes() < 12) {
            return null;
        } else if (in.readableBytes() > 256) {
            throw new TooLongFrameException();
        } else {
            ByteBuf byteBuf = in.slice(0, 12);
            byteBuf.readBytes(3);
            int length = byteBuf.readUnsignedShortLE();
            if (in.readableBytes() < (length + 3)) {
                in.resetReaderIndex();
                return null;
            }
            return in.readBytes(length + 3 + 2);
        }
    }
}
