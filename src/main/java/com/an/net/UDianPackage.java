package com.an.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public class UDianPackage {
    private static final AtomicInteger seq=new  AtomicInteger();

    private String dny = "DNY";
    private short length;
    private int physicalId;
    private short messageId;
    private byte command;

    private byte[] data;

    private short check;

    public short calLength() {
        return (short) (4 + 2 + 1 + data.length + 2);
    }

    public int getCheck() {
        return check;
    }

    public byte getDeviceType() {
        return (byte) (physicalId >> 24);
    }

    public int getDeviceCode() {
        return physicalId & 0x00FFFFFF;
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
    public UDianPackage(int physicalId,byte command,byte []data ){
        this.dny="DNY";
        this.physicalId = physicalId;
        this.setMessageId(getMessageId());
        this.setCommand(this.command);
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
        uDianPackage.setCheck((short) 0x02);
        return uDianPackage;
    }

    public static UDianPackage buildFromHexString(String hexString) {
        ByteBuf buffer = Unpooled.buffer(hexString.length() / 2);
        buffer.writeBytes(DatatypeConverter.parseHexBinary(hexString));
        return AP3000Codec.getYouDianPackage(buffer);
    }

    private static int generateMessageId(){
        return  (seq.getAndDecrement()&0xFFFF);
    }
}
