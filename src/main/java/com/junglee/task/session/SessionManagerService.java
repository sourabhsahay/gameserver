package com.junglee.task.session;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface SessionManagerService<T>
{
    public Session getSession(T key);

    public boolean putSession(T key, Session session);

    public boolean removeSession(T key);
    // Add a session type object also to get udp/tcp/any
}