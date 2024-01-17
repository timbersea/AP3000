package com.an.net;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalContext {
    private static Map<Integer, ChannelHandlerContext> deviceCodeChannelContext=new ConcurrentHashMap<>(1024);

    public static void put(Integer deviceCode,ChannelHandlerContext context){
        deviceCodeChannelContext.put(deviceCode,context);
    }
}
