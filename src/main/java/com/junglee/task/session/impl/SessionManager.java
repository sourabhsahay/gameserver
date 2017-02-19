package com.junglee.task.session.impl;

import com.junglee.task.session.Session;
import com.junglee.task.session.SessionManagerService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 * Can be persisted in key-value store,but maintaining in Map for illustration
 */
@Service
public class SessionManager<T> implements SessionManagerService<T>
{
    protected final Map<T, Session> sessions;

    public SessionManager()
    {
        sessions = new ConcurrentHashMap<T, Session>(1000);
    }

    public Session getSession(T key)
    {
        return sessions.get(key);
    }

    public boolean putSession(T key, Session session)
    {
        if(null == key ||  null == session)
        {
            return false;
        }

        if(null == sessions.put(key, session))
        {
            return true;
        }
        return false;
    }

    public boolean removeSession(Object key)
    {
        return null != sessions.remove(key) ? true : false;
    }

}