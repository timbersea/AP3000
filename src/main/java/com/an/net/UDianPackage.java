package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class UDianPackage {
    // 定义协议常量（根据实际协议调整含义）
    public static final int HEADER_SKIP_BYTES = 3; // 需跳过的头部字节数
    public static final int PHYSICAL_ID_LENGTH = 4; // 物理ID长度（字节）
    public static final int MESSAGE_ID_LENGTH = 2; // 消息ID长度（字节）
    public static final int COMMAND_LENGTH = 1; // 命令字段长度（字节）
    public static final int CHECK_LENGTH = 2; // 校验值长度（字节）
    public static final int FRAME_LENGTH = 2; // 消息长度丙个字节（字节）
    public static final int SIM_CARD_LENGTH = 20;
    public static final int MAX_LENGTH = 256;
    private static final AtomicInteger seq = new AtomicInteger();

    /**
     * 固定标识包头
     */
    private String dny = "DNY";
    private short length;
    private int physicalId;
    private int pileCode;
    private short messageId;
    private int command;//协议中实际占一个字节

    /**
     * 业务数据
     */
    private ByteBuf data;

    private short check;

    public short calLength() {
        return (short) (4 + 2 + 1 + data.readableBytes() + 2);
    }

    public int getCheck() {
        return check;
    }

    public int getDeviceType() {
        return (physicalId & 0xFF);

    }

    public int getPileCode() {
        return physicalId2PileCode();
    }

    public String getDny() {
        return dny;
    }

    public void setDny(String dny) {
        this.dny = dny;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public Integer getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(int physicalId) {
        this.physicalId = physicalId;
    }

    public short getMessageId() {
        return messageId;
    }

    public void setMessageId(short messageId) {
        this.messageId = messageId;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    public void setCheck(short check) {
        this.check = check;
    }

    public void setPileCode(int pileCode) {
        this.pileCode = pileCode;
    }

    @Override
    public String toString() {
        return "YouDianPackage{" +
                "dny='" + dny + '\'' +
                ", length=" + length +
                ", physicalId=" + physicalId2PileCode() +
                ", messageId=" + messageId +
                ", command=0x" + Integer.toHexString(command) +
                ", data=" + ByteBufUtil.hexDump(data) +
                ", check=" + check +
                '}';
    }

    public String toHexString() {
        ByteBuf out = Unpooled.buffer(this.length);
        out.writeBytes(this.dny.getBytes(StandardCharsets.UTF_8));
        out.writeShortLE(this.length);
        out.writeIntLE(this.physicalId);
        out.writeShortLE(this.messageId);
        out.writeByte(this.command);
        out.writeBytes(this.data);
        out.writeShortLE(this.check);
        byte[] bytes = new byte[out.readableBytes()];
        out.readBytes(bytes);
        ReferenceCountUtil.release(out);
        return DatatypeConverter.printHexBinary(bytes);
    }


    public UDianPackage() {
    }

    public UDianPackage(int pileCode, byte command, ByteBuf data) {
        this.dny = "DNY";
        this.pileCode = pileCode;
        this.setMessageId(generateMessageId());
        this.setCommand(command);
        this.setData(data);
        this.setLength((calLength()));
    }

    public UDianPackage getReply(ByteBuf data) {
        UDianPackage uDianPackage = new UDianPackage();
        uDianPackage.dny = this.dny;
        uDianPackage.physicalId = this.physicalId;
        uDianPackage.setMessageId(this.messageId);
        uDianPackage.setCommand(this.command);
        uDianPackage.setData(data);
        uDianPackage.setLength((uDianPackage.calLength()));
        uDianPackage.setCheck((short)AP3000Codec.calCheck(uDianPackage));
        return uDianPackage;
    }

    public static UDianPackage buildFromHexString(String hexString) {
        byte[] bytes = ByteBufUtil.decodeHexDump(hexString);
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(bytes);
        return AP3000Codec.getYouDianPackage(buffer);
    }

    public static short generateMessageId() {
        return (short) (seq.getAndIncrement() & 0x07FFF);
    }


    /**
     * 通用长度为1，内容为0的回复消息
     * @return
     */
    public static final ByteBuf byteBufZero(){
        ByteBuf reply = Unpooled.buffer(1);
        reply.writeByte(0);
        return reply;
    }

    /**
     * physicalId映射成业务系统中的pileCode,因为设备传上的四个字节是由 deviceType的识别码和设备编号组成的，
     * 所以需要做额外的解析
     * @return
     */
    public  final int physicalId2PileCode() {
        int pileCode = physicalId & 0x00FFFFFF;
        return pileCode;
    }
    public static int physicalId2PileCode(int physicalId){
        return physicalId & 0x00FFFFFF;
    }
    /**
     * pileCode转physicalId
     * @param pileCode 业务系统使用pileCode标识设备
     * @param deviceTypeAttr 设备型号
     * @return 设备与服务器通信识别的physicalId
     */
    public static int pileCode2PhysicalId(Integer pileCode,byte deviceTypeAttr){
        ByteBuf buffer = Unpooled.buffer(4);
        buffer.writeByte(pileCode&0xFF);
        buffer.writeByte((pileCode&0xFF00)>>8);
        buffer.writeByte((pileCode&0xFF0000)>>16);
        buffer.writeByte(deviceTypeAttr);
        int physicalId = buffer.readIntLE();
        ReferenceCountUtil.release(buffer);
        return physicalId;
    }

    public  final byte physicalId2Type(){
        return (byte) ((physicalId&0xFF000000)>>24);
    }
}
