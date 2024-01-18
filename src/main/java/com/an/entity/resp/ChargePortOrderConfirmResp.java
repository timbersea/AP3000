package com.an.entity.resp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargePortOrderConfirmResp extends BaseResp{
    private byte port;
    private byte replay;

    @Override
    public byte[] data() {
        ByteBuf buffer = Unpooled.buffer(2);
        buffer.writeIntLE(port);
        buffer.writeByte(replay);
        return buffer.array();
    }
}
