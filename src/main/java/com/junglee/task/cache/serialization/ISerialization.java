package com.junglee.task.cache.serialization;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface ISerialization<T> {

    byte[] serialize(T object);

    T deserialize(byte[] bytes, Object o, Class cacheClass);
}
