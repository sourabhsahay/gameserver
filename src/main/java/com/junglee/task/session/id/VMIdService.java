package com.junglee.task.session.id;

import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
@Service
public class VMIdService implements UniqueIDService
{
    //Should be replaced by better Id generator if required.
    public static final AtomicLong ID = new AtomicLong(0l);

    public Object generate() throws Exception
    {
        String nodeName = InetAddress.getLocalHost().getHostName();
        if (null == nodeName || "".equals(nodeName))
        {
            return ID.incrementAndGet();
        }
        else
        {
            return nodeName + ID.incrementAndGet();
        }
    }

    public Object generateFor(@SuppressWarnings("rawtypes") Class objectClass)
    {
        return objectClass.getSimpleName() + ID.incrementAndGet();
    }

}
