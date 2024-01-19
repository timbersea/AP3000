package com.an.net;

import io.netty.channel.ChannelHandlerContext;
import org.apache.thrift.TException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalContext {
    private static Map<Integer, ChannelHandlerContext> deviceCodeChannelContext=new ConcurrentHashMap<>(1024);
    private static Map<Integer, CompletableFuture> completableFutureMap=new ConcurrentHashMap<>(1024);

    public static void put(Integer deviceCode,ChannelHandlerContext context){
        deviceCodeChannelContext.put(deviceCode,context);
    }

    public static UDianPackage writeData(Integer physicalId, UDianPackage uDianPackage) throws TException {
        ChannelHandlerContext channelHandlerContext = deviceCodeChannelContext.get(physicalId);
        if(channelHandlerContext==null){
            throw new TException(physicalId+ " is not connect to server");
        }
        channelHandlerContext.writeAndFlush(uDianPackage);
        return uDianPackage;
    }
}
