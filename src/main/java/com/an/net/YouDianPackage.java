package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

public class YouDianPackage {
    private String dny = "DNY";
    private short length;
    private int physicalId;
    private short messageId;
    private byte command;

    private byte[] data;
    private short check;

    public String getDny() {
        return dny;
    }

    public void setDny(String dny) {
        this.dny = dny;
    }

    public int getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public int getPhysicalId() {
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

    public void setCommand(byte command) {
        this.command = command;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(short check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "YouDianPackage{" +
                "dny='" + dny + '\'' +
                ", length=" + length +
                ", physicalId=" + physicalId +
                ", messageId=" + messageId +
                ", command=" + command +
                ", data=" + DatatypeConverter.printHexBinary(data) +
                ", check=" + check +
                '}';
    }

    public String toHexString() {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(this.getDny().getBytes(StandardCharsets.UTF_8));
        out.writeShortLE(this.getLength());
        out.writeIntLE(this.getPhysicalId());
        out.writeShortLE(this.getMessageId());
        out.writeByte(this.getCommand());
        out.writeBytes(this.getData());
        out.writeShortLE(this.getCheck());
        byte[] bytes = new byte[out.readableBytes()];
        out.readBytes(bytes);
        return DatatypeConverter.printHexBinary(bytes);
    }


    public YouDianPackage() {
    }

    public YouDianPackage getReply(YouDianPackage req){
        YouDianPackage youDianPackage = new YouDianPackage();
        youDianPackage.dny=req.dny;
        youDianPackage.physicalId=req.physicalId;
        youDianPackage.setMessageId(req.getMessageId());
        youDianPackage.setCommand(req.command);
        return youDianPackage;
    }

    public static YouDianPackage buildFromHexString(String hexString) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(hexString.length()/2);
        buffer.writeBytes(DatatypeConverter.parseHexBinary(hexString));
        return AP3000Codec.getYouDianPackage(buffer);
    }
}
