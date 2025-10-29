package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDianPackageParser {
    private static final Logger log = LoggerFactory.getLogger(UDianPackageParser.class);

    // 定义协议常量（根据实际协议调整含义）
    private static final int HEADER_SKIP_BYTES = 3; // 需跳过的头部字节数
    private static final int PHYSICAL_ID_LENGTH = 4; // 物理ID长度（字节）
    private static final int MESSAGE_ID_LENGTH = 2; // 消息ID长度（字节）
    private static final int COMMAND_LENGTH = 1; // 命令字段长度（字节）
    private static final int CHECK_LENGTH = 2; // 校验值长度（字节）
    private static final int FRAME_LENGTH = 2; // 消息长度丙个字节（字节）

    public static UDianPackage getYouDianPackage(ByteBuf decoded) {
        // 校验输入缓冲区合法性
        if (decoded == null || !decoded.isReadable()) {
            throw new IllegalArgumentException("Decoded buffer is null or unreadable");
        }

        // 保存初始读指针位置，便于异常时定位问题
        int initialReaderIndex = decoded.readerIndex();

        try {
            // 1. 计算校验值（排除最后2字节的校验值）
            int checkValueStart = decoded.readableBytes() - CHECK_LENGTH;
            if (checkValueStart < 0) {
                throw new IllegalArgumentException("Buffer too short to contain check value");
            }
            byte[] toCalCheck = new byte[checkValueStart];
            decoded.getBytes(0, toCalCheck); // 从0开始读取到校验值前

            // 2. 解析协议字段
            decoded.skipBytes(HEADER_SKIP_BYTES);
            int length = decoded.readUnsignedShortLE(); // 长度字段

            // 校验缓冲区长度是否足够（避免后续读取越界）
            if (decoded.readableBytes() < (PHYSICAL_ID_LENGTH + MESSAGE_ID_LENGTH + COMMAND_LENGTH + (length - HEADER_SKIP_BYTES - 2 - PHYSICAL_ID_LENGTH - MESSAGE_ID_LENGTH - COMMAND_LENGTH) + CHECK_LENGTH)) {
                throw new IllegalArgumentException("Buffer length insufficient for parsing, required: " + length + ", remaining: " + decoded.readableBytes());
            }

            int physicalId = decoded.readIntLE(); // 物理ID
            int messageId = decoded.readUnsignedShortLE(); // 消息ID
            int command = decoded.readByte(); // 命令

            // 计算数据字段长度（总长度 - 已读固定字段长度）
            int dataLength = length - HEADER_SKIP_BYTES - 2 /*length字段自身长度*/ - PHYSICAL_ID_LENGTH - MESSAGE_ID_LENGTH - COMMAND_LENGTH - CHECK_LENGTH;
            ByteBuf data = Unpooled.buffer(dataLength);
            decoded.readBytes(data); // 读取数据字段

            int check = decoded.readUnsignedShortLE(); // 读取校验值

            // 3. 校验校验值
            int calCheckValue = calCheck(toCalCheck);
            if (calCheckValue != check) {
                log.debug("Check value mismatch - calculated: [{}], received: [{}], buffer: {}",
                        calCheckValue, check, decoded.toString());
                throw new IllegalArgumentException(String.format(
                        "Check value mismatch: calculated=%d, received=%d (offset=%d)",
                        calCheckValue, check, initialReaderIndex));
            }

            // 4. 构建返回对象
            UDianPackage uDianPackage = new UDianPackage();
            uDianPackage.setDny("DNY");
            uDianPackage.setLength((short) length);
            uDianPackage.setPhysicalId(physicalId);
            uDianPackage.setMessageId((short) messageId);
            uDianPackage.setCommand(command);
            uDianPackage.setData(data);
            uDianPackage.setCheck((short) check);

            return uDianPackage;

        } catch (Exception e) {
            // 重置读指针，便于上游重试解析（若需要）
            decoded.readerIndex(initialReaderIndex);
            log.error("Failed to parse UDianPackage (offset={})", initialReaderIndex, e);
            throw e;
        } finally {
            // 注意：若decoded由上游管理（如Netty的ByteBuf），不应在此释放，否则会导致上游引用失效
            // ReferenceCountUtil.release(decoded);
        }
    }


    public static UDianPackage getYouDianPackageOld(ByteBuf decoded) {
        ByteBuf data = null;
        try {
            byte[] toCalCheck = new byte[decoded.readableBytes() - CHECK_LENGTH];//去掉最后两字节的检校值后的数据参与计算校验值
            decoded.getBytes(0, toCalCheck, 0, toCalCheck.length);
            decoded.skipBytes(HEADER_SKIP_BYTES);
            int length = decoded.readUnsignedShortLE();
            int physicalId = decoded.readIntLE();
            int messageId = decoded.readUnsignedShortLE();
            int command = decoded.readByte();
            data = decoded.readBytes(length - FRAME_LENGTH- PHYSICAL_ID_LENGTH - MESSAGE_ID_LENGTH - COMMAND_LENGTH );
            data.retain();
            int check = decoded.readUnsignedShortLE();

            UDianPackage uDianPackage = new UDianPackage();
            uDianPackage.setDny("DNY");
            uDianPackage.setLength((short) length);
            uDianPackage.setPhysicalId(physicalId);
            uDianPackage.setMessageId((short) messageId);
            uDianPackage.setCommand(command);
            ByteBuf bytes = Unpooled.buffer(length - 4 - 2 - 1 - 2);
            data.readBytes(bytes);
            uDianPackage.setData(bytes);
            uDianPackage.setCheck((short) check);

            int calCheckValue = calCheck(toCalCheck);
            if (calCheckValue != check) {
                log.debug("cal check value :[{}],receive checkValue[{}]", calCheckValue, check);
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

    // 假设的校验值计算方法（根据实际协议实现）
    private static int calCheck(byte[] data) {
        // 实际逻辑需替换为协议定义的校验算法（如CRC、异或等）
        int check = 0;
        for (byte b : data) {
            check ^= b & 0xFF;
        }
        return check;
    }
}
