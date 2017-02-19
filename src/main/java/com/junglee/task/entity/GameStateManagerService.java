package com.junglee.task.entity;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface GameStateManagerService
{
    public Object getState();

    public boolean compareAndSetSyncKey(Object key);

    public Object getAndSetState(Object state);

    public boolean compareAndSetState(Object syncKey, Object state);

    public Object getSyncKey();

    public byte[] getSerializedByteArray();

    public void setSerializedByteArray(byte[] serializedBytes)
            throws UnsupportedOperationException;

}

