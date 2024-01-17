package com.an.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageDispatcher {
    private static Map<Byte, AbstractService> r = new ConcurrentHashMap<>();

    static {
     //   r.put((byte) 0x01, new HeatBeatService());
    }


    public static AbstractService getService(byte t) {
        return r.get(t);
    }
}
