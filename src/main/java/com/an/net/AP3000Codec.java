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
import static com.an.net.UDianPackage.MAX_LENGTH;
import static com.an.net.UDianPackage.MESSAGE_ID_LENGTH;
import static com.an.net.UDianPackage.PHYSICAL_ID_LENGTH;
import static com.an.net.UDianPackage.SIM_CARD_LENGTH;

/**
 * AP3000的codec实现
 */
public class AP3000Codec extends ByteToMessageCodec<UDianPackage> {
    public static final String LINK = "6C696E6B";
    public static final int LINK_LENGTH = 4;
    private static final int MIN_LENGTH_FIELD =
            PHYSICAL_ID_LENGTH + MESSAGE_ID_LENGTH + COMMAND_LENGTH + CHECK_LENGTH;
    private static final Logger log = LoggerFactory.getLogger(AP3000Codec.class);
    private static final AttributeKey<String> simAttr = AttributeKey.valueOf("simNo");

    public static UDianPackage getYouDianPackage(ByteBuf decoded) {
        int initialReaderIndex = decoded.readerIndex();
        byte[] toCalCheck = new byte[decoded.readableBytes() - CHECK_LENGTH];
        decoded.getBytes(decoded.readerIndex(), toCalCheck, 0, toCalCheck.length);

        decoded.skipBytes(HEADER_SKIP_BYTES);
        int length = decoded.readUnsignedShortLE();
        int physicalId = decoded.readIntLE();
        int messageId = decoded.readUnsignedShortLE();
        int command = decoded.readByte();

        int dataLength = length - PHYSICAL_ID_LENGTH - MESSAGE_ID_LENGTH - COMMAND_LENGTH - CHECK_LENGTH;
        if (dataLength < 0) {
            throw new IllegalArgumentException("invalid frame data length: " + dataLength);
        }
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

        int calCheckValue = calCheck(toCalCheck);
        if (!Objects.equals(calCheckValue, check)) {
            throw new IllegalArgumentException(String.format("pileCode=%d,Check value mismatch: calculated=%d, " +
                            "received=%d (offset=%d),frame data=%s",
                    UDianPackage.physicalId2PileCode(physicalId), calCheckValue, check, initialReaderIndex,
                    ByteBufUtil.hexDump(decoded)));
        }
        return uDianPackage;
    }

    /**
     * 校验和：字节累加后取低 16 位（与线上 short 一致）
     */
    public static int calCheck(byte[] data) {
        int sum = 0;
        for (byte b : data) {
            sum += (b & 0xFF);
        }
        return sum & 0xFFFF;
    }

    public static int calCheck(UDianPackage uDianPackage) {
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
            ReferenceCountUtil.release(out);
        }
    }

    private static int indexOfDny(ByteBuf in) {
        int from = in.readerIndex();
        int to = in.writerIndex() - 2;
        for (int i = from; i < to; i++) {
            if (in.getByte(i) == 'D' && in.getByte(i + 1) == 'N' && in.getByte(i + 2) == 'Y') {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UDianPackage msg, ByteBuf out) {
        log.debug("send to pileCode:[{}] msg:[{}]", channelHandlerContext.channel().attr(GlobalContext.pileCodeAttr),
                msg);
        out.writeBytes(msg.getDny().getBytes(StandardCharsets.UTF_8));
        if (msg.getLength() > MAX_LENGTH) {
            throw new TooLongFrameException("length must less than or equal " + MAX_LENGTH + " " + msg.toHexString());
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
        tryConsumeSim(channelHandlerContext, byteBuf);
        tryConsumeLink(channelHandlerContext, byteBuf);

        ByteBuf decoded = readFrame(byteBuf);
        if (decoded != null) {
            try {
                list.add(getYouDianPackage(decoded));
            } finally {
                ReferenceCountUtil.release(decoded);
            }
        }
    }

    private void tryConsumeSim(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (ctx.channel().attr(simAttr).get() != null) {
            return;
        }
        if (byteBuf.readableBytes() < SIM_CARD_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();
        ByteBuf simCardNo = byteBuf.readBytes(SIM_CARD_LENGTH);
        try {
            String simNo = ByteBufUtil.hexDump(simCardNo);
            if ("38393836".equals(simNo.substring(0, 8))) {
                log.info("decode:channel = [{}], simNo = [{}]", ctx.channel(), simNo);
                ctx.channel().attr(simAttr).setIfAbsent(simNo);
            } else {
                byteBuf.resetReaderIndex();
            }
        } finally {
            ReferenceCountUtil.release(simCardNo);
        }
    }

    private void tryConsumeLink(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < LINK_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();
        ByteBuf linkBuf = byteBuf.readBytes(LINK_LENGTH);
        try {
            String link = ByteBufUtil.hexDump(linkBuf);
            if (LINK.equalsIgnoreCase(link)) {
                log.debug("pileCode = [{}],read link [{}]",
                        ctx.channel().attr(GlobalContext.pileCodeAttr), link);
            } else {
                byteBuf.resetReaderIndex();
            }
        } finally {
            ReferenceCountUtil.release(linkBuf);
        }
    }

    /**
     * 定位 DNY magic 后读取完整帧；非法 length 丢弃 1 字节继续同步。
     */
    private ByteBuf readFrame(ByteBuf in) {
        while (true) {
            int dnyIndex = indexOfDny(in);
            if (dnyIndex < 0) {
                if (in.readableBytes() > 2) {
                    // 保留末尾最多 2 字节，可能是不完整 magic
                    in.skipBytes(in.readableBytes() - 2);
                }
                return null;
            }
            if (dnyIndex > in.readerIndex()) {
                in.readerIndex(dnyIndex);
            }

            int minHeaderLength =
                    HEADER_SKIP_BYTES + FRAME_LENGTH + PHYSICAL_ID_LENGTH + MESSAGE_ID_LENGTH + COMMAND_LENGTH;
            if (in.readableBytes() < minHeaderLength) {
                return null;
            }

            in.markReaderIndex();
            in.skipBytes(HEADER_SKIP_BYTES);
            int length = in.readUnsignedShortLE();
            in.resetReaderIndex();

            if (length < MIN_LENGTH_FIELD || length > MAX_LENGTH) {
                log.warn("invalid frame length [{}], resync", length);
                in.skipBytes(1);
                continue;
            }

            int fullFrameLength = HEADER_SKIP_BYTES + FRAME_LENGTH + length;
            if (in.readableBytes() < fullFrameLength) {
                return null;
            }
            return in.readBytes(fullFrameLength);
        }
    }
}
