package com.an.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractService<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractService.class);

    private static Map<Byte, AbstractService> r = new ConcurrentHashMap();

    static {
        r.put((byte) 0x01, new HeatBeatService());
    }

    Class<T> clazz;

    {
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type actualTypeArgument = pt.getActualTypeArguments()[0];
            clazz = (Class<T>) actualTypeArgument;
            log.info("[{}] receive msg type is [{}]", this.getClass().getSimpleName(), clazz.getSimpleName());
        } catch (Exception e) {
            log.warn("parse generic params error at {}", this);
        }
    }

    /**
     * @param data 收到的消息
     * @return 回复的消息
     */
    public byte[] onReceive(byte[] data) {
        T t = parseData(data);
        return doService(t);
    }

    protected abstract byte[] doService(T t);

    protected abstract T parseData(byte[] data);


}
