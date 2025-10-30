package com.an;

import com.an.net.UDianPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class SimpleTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    @Test
    public void test0(){
        for (int i = 0; i < 100; i++) {
            String format = String.format("%016d", Long.parseLong("1726839741913346048"));
            log.info("test0:{}",format);
        }
    }
    public void test1(){
        byte[] bytes = "134.175.12.227".getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = new byte[46];
        System.arraycopy(bytes,0,bytes1,0,bytes.length);
        log.info("test1:{}", DatatypeConverter.printHexBinary(bytes1));
    }
    @Test
    public void test2(){
        UDianPackage uDianPackage = new UDianPackage();
        //高1个字节04表示双路，低3字节0xD728D6=14100694，和设备二维码下面的数字对应。
        int physicalId = UDianPackage.pileCode2PhysicalId(14100694, (byte) 04);
        uDianPackage.setPhysicalId(physicalId);
        log.info("{}",uDianPackage.getPileCode());
        //4d728d6
        log.info(Integer.toHexString(physicalId));


        // 1. 创建ByteBuf并设置为小端序
        ByteBuf buf = Unpooled.buffer(4).order(ByteOrder.LITTLE_ENDIAN);

        // 2. 按小端序写入int（4字节）
        buf.writeInt(physicalId);

        String s = ByteBufUtil.hexDump(buf);
        log.info(s);
    }


}
