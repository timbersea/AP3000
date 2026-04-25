package com.an;

import com.an.dto.ChargeOrderConfirm;
import com.an.net.UDianPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SimpleTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    @Test
    public void test1() {
        byte[] bytes = "134.175.12.227".getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = new byte[46];
        System.arraycopy(bytes, 0, bytes1, 0, bytes.length);
        log.info("test1:{}", ByteBufUtil.hexDump(bytes1));
    }

    @Test
    public void test2() {
        UDianPackage uDianPackage = new UDianPackage();
        //高1个字节04表示双路，低3字节0xD728D6=14100694，和设备二维码下面的数字对应。
        int physicalId = UDianPackage.pileCode2PhysicalId(14100694, (byte) 0x04);
        uDianPackage.setPhysicalId(physicalId);
        log.info("{}", uDianPackage.getPileCode());
        //4d728d6
        log.info(Integer.toHexString(physicalId));


        // 1. 创建ByteBuf并设置为小端序
        ByteBuf buf = Unpooled.buffer(4);

        // 2. 按小端序写入int（4字节）
        buf.writeIntLE(physicalId);

        String s = ByteBufUtil.hexDump(buf);
        log.info(s);
    }

    @Test
    public void test() {
        ArrayList<Object> objects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ChargeOrderConfirm chargeOrderConfirm = new ChargeOrderConfirm();
            objects.add(chargeOrderConfirm);
        }
    }

}
