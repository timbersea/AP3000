package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.an.net.UDianPackage.CHECK_LENGTH;
import static com.an.net.UDianPackage.COMMAND_LENGTH;
import static com.an.net.UDianPackage.FRAME_LENGTH;
import static com.an.net.UDianPackage.HEADER_SKIP_BYTES;
import static com.an.net.UDianPackage.MESSAGE_ID_LENGTH;
import static com.an.net.UDianPackage.PHYSICAL_ID_LENGTH;
import static com.an.net.UDianPackage.SIM_CARD_LENGTH;

/**
 * AP3000的codec实现
 */
public class AP3000Codec extends ByteToMessageCodec<UDianPackage> {
    public static final String LINK = "6C696E6B";
    public static final int LINK_LENGTH = 4;
    private static final Logger log = LoggerFactory.getLogger(AP3000Codec.class);
    private static final AttributeKey<String> simAttr = AttributeKey.valueOf("simNo");

    public static UDianPackage getYouDianPackage(ByteBuf decoded) {
        int initialReaderIndex = decoded.readerIndex();
        ByteBuf dataBuf = null;
        try {
            // 计算校验和数据
            byte[] toCalCheck = new byte[decoded.readableBytes() - CHECK_LENGTH];
            decoded.getBytes(0, toCalCheck, 0, toCalCheck.length);

            decoded.skipBytes(HEADER_SKIP_BYTES);
            int length = decoded.readUnsignedShortLE();
            int physicalId = decoded.readIntLE();
            int messageId = decoded.readUnsignedShortLE();
            int command = decoded.readByte();

            // 修复：正确读取数据，简化字节操作
            int dataLength = length - PHYSICAL_ID_LENGTH - MESSAGE_ID_LENGTH - COMMAND_LENGTH - FRAME_LENGTH;
            byte[] data = new byte[dataLength];
            decoded.readBytes(data);

            int check = decoded.readUnsignedShortLE();

            UDianPackage uDianPackage = new UDianPackage();
            uDianPackage.setDny("DNY");
            uDianPackage.setLength((short) length);
            uDianPackage.setPhysicalId(physicalId);
            uDianPackage.setMessageId((short) messageId);
            uDianPackage.setCommand(command);
            uDianPackage.setData(data);
            uDianPackage.setCheck((short) check);

            // 校验和验证
            int calCheckValue = calCheck(toCalCheck);
            if (!Objects.equals(calCheckValue, check)) {
                throw new IllegalArgumentException(String.format("pileCode=%d,Check value mismatch: calculated=%d, " +
                                "received=%d (offset=%d),frame data=%s",
                        UDianPackage.physicalId2PileCode(physicalId), calCheckValue, check, initialReaderIndex,
                        ByteBufUtil.hexDump(decoded)));
            }
            return uDianPackage;
        } finally {
            // 确保所有临时资源释放（移除无用dataBuf，简化代码）
            ReferenceCountUtil.release(dataBuf);
        }
    }

    /**
     * 校验和计算（基础方法）
     */
    private static int calCheck(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum += (b & 0xFF);
        }
        return sum;
    }

    /**
     * 校验和计算（对象方法）【修复：内存泄漏核心点】
     */
    public static int calCheck(UDianPackage uDianPackage) {
        // 临时ByteBuf必须释放
        ByteBuf out = Unpooled.buffer();
        try {
            out.writeBytes(uDianPackage.getDny().getBytes(StandardCharsets.UTF_8));
            out.writeShortLE(uDianPackage.getLength());
            out.writeIntLE(uDianPackage.getPhysicalId());
            out.writeShortLE(uDianPackage.getMessageId());
            out.writeByte(uDianPackage.getCommand());
            out.writeBytes(uDianPackage.getData());

            byte[] toCalCheck = new byte[out.readableBytes()];
            out.readBytes(toCalCheck);
            return calCheck(toCalCheck);
        } finally {
            // 强制释放堆外内存
            ReferenceCountUtil.release(out);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UDianPackage msg, ByteBuf out) {
        log.debug("send to pileCode:[{}] msg:[{}]", channelHandlerContext.channel().attr(GlobalContext.pileCodeAttr),
                msg);
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
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        // 处理SIM卡号（修复：临时ByteBuf手动释放）
        if (channelHandlerContext.channel().attr(simAttr).get() == null) {
            if (byteBuf.readableBytes() >= SIM_CARD_LENGTH) {
                byteBuf.markReaderIndex();
                // 临时ByteBuf必须释放
                ByteBuf simCardNo = byteBuf.readBytes(SIM_CARD_LENGTH);
                try {
                    String simNo = ByteBufUtil.hexDump(simCardNo);
                    if ("38393836".equals(simNo.substring(0, 8))) {
                        log.info("decode:channel = [{}], simNo = [{}]", channelHandlerContext.channel(), simNo);
                        channelHandlerContext.channel().attr(simAttr).setIfAbsent(simNo);
                    } else {
                        byteBuf.resetReaderIndex();
                    }
                } finally {
                    // 强制释放
                    ReferenceCountUtil.release(simCardNo);
                }
            }
        } else {
            // 处理心跳包link（修复：临时ByteBuf手动释放）
            if (byteBuf.readableBytes() >= LINK_LENGTH) {
                byteBuf.markReaderIndex();
                ByteBuf linkBuf = byteBuf.readBytes(LINK_LENGTH);
                try {
                    String link = ByteBufUtil.hexDump(linkBuf);
                    if (LINK.equalsIgnoreCase(link)) {
                        log.debug("pileCode = [{}],read link [{}]",
                                channelHandlerContext.channel().attr(GlobalContext.pileCodeAttr), link);
                    } else {
                        byteBuf.resetReaderIndex();
                    }
                } finally {
                    // 强制释放
                    ReferenceCountUtil.release(linkBuf);
                }
            }
            // 读取完整数据帧
            ByteBuf decoded = readFrame(byteBuf);
            if (decoded != null) {
                try {
                    UDianPackage uDianPackage = getYouDianPackage(decoded);
                    list.add(uDianPackage);
                } finally {
                    // 双重保险：确保帧数据一定释放
                    ReferenceCountUtil.release(decoded);
                }
            }
        }
    }

    /**
     * 读取完整数据帧（无内存泄漏，优化可读性）
     */
    private ByteBuf readFrame(ByteBuf in) {
        in.markReaderIndex();
        int minHeaderLength =
                HEADER_SKIP_BYTES + FRAME_LENGTH + PHYSICAL_ID_LENGTH + MESSAGE_ID_LENGTH + COMMAND_LENGTH;
        if (in.readableBytes() < minHeaderLength) {
            return null;
        }

        in.skipBytes(HEADER_SKIP_BYTES);
        short length = in.readShortLE();
        in.resetReaderIndex();

        // 校验完整帧长度
        int fullFrameLength = HEADER_SKIP_BYTES + FRAME_LENGTH + length;
        if (in.readableBytes() < fullFrameLength) {
            return null;
        }

        return in.readBytes(fullFrameLength);
    }
}