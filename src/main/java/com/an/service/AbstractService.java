package com.an.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public abstract class AbstractService<Req,Resp> {
    private static final Logger log = LoggerFactory.getLogger(AbstractService.class);

    private  final Map<Byte, AbstractService<Req,Resp> > r = new ConcurrentHashMap<>();

    Class<Req> clazz;

    {
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            Type actualTypeArgument = pt.getActualTypeArguments()[0];
            //noinspection unchecked
            clazz = (Class<Req>) actualTypeArgument;
            log.info("[{}] receive msg type is [{}]", this.getClass().getSimpleName(), clazz.getSimpleName());
        } catch (Exception e) {
            log.warn("parse generic params error at {}", this);
        }
    }

    /**
     * @param data 收到的消息
     * @return 回复的消息
     */
    public final Resp onReceive(byte[] data) {
        Req t = parseData(data);
        return doService(t);
    }

    protected abstract Resp doService(Req req);

    protected abstract Req parseData(byte[] data);


}
