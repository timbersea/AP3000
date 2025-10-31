package com.an.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageDispatcher {
    private static final Map<Byte, AbstractService> r = new ConcurrentHashMap<>();

    static {
    }


    public static AbstractService getService(byte t) {
        return r.get(t);
    }
}
