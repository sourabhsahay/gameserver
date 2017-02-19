package com.junglee.task.cache.serialization;

import java.io.*;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class DefaultSerializer<T>  {


    public byte[] serialize(T object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream out = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(object);

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public T deserialize(byte[] bytes, Object o, Class<T> cacheClass) {
        ByteArrayInputStream fis = null;
        ObjectInputStream in = null;
        T cachedObject=null;

        try {
            fis = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(fis);
            cachedObject = cacheClass.cast( in.readObject());
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cachedObject;
    }
}
