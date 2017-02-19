package com.junglee.task.cache.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public class KryoSerializer {

    public static<T> byte[] serialize(T object) throws IOException{
        Kryo kryo = new Kryo();
        ByteArrayOutputStream outputStream  = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeObject(output, object);
        output.close();
        return outputStream.toByteArray();
    }

    public static <T> T deserialize(byte[] bytes, Object o, Class<T> cacheClass) {
        Kryo kryo = new Kryo();
        Input input = new Input(new ByteArrayInputStream(bytes));
        T someObject = kryo.readObject(input, cacheClass);
        input.close();
        return someObject;
    }
}
