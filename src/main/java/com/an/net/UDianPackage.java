package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.Data;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class UDianPackage {
    private static final AtomicInteger seq = new AtomicInteger();

    private String dny = "DNY";
    private short length;
    private Integer physicalId;
    private int pileCode;
    private short messageId;
    private int command;//协议中实际占一个字节

    private byte[] data;

    private short check;

    public short calLength() {
        return (short) (4 + 2 + 1 + data.length + 2);
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setCheck(short check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "YouDianPackage{" +
                "dny='" + dny + '\'' +
                ", length=" + length +
                ", physicalId=" + physicalId2PileCode() +
                ", messageId=" + messageId +
                ", command=0x" + Integer.toHexString(command) +
                ", data=" + DatatypeConverter.printHexBinary(data) +
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

    public UDianPackage(int pileCode, byte command, byte[] data) {
        this.dny = "DNY";
        this.pileCode = pileCode;
        this.setMessageId(generateMessageId());
        this.setCommand(command);
        this.setData(data);
        this.setLength((calLength()));
    }

    public UDianPackage getReply(byte[] data) {
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
        ByteBuf buffer = Unpooled.buffer(hexString.length() / 2);
        buffer.writeBytes(DatatypeConverter.parseHexBinary(hexString));
        return AP3000Codec.getYouDianPackage(buffer);
    }

    public static short generateMessageId() {
        return (short) (seq.getAndIncrement() & 0x07FFF);
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
    public  final byte physicalId2Type(){
        return (byte) ((physicalId&0xFF000000)>>24);
    }
}
