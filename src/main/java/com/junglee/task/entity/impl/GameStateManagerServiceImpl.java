package com.junglee.task.entity.impl;

import com.junglee.task.entity.GameStateManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vishwasourabh.sahay on 19/02/17.
 */
@Service
public class GameStateManagerServiceImpl implements GameStateManagerService
{
    private static final Logger LOG = LoggerFactory.getLogger(GameStateManagerServiceImpl.class);

    private Object state;
    byte [] serializedBytes;
    private AtomicInteger syncKey;

    public GameStateManagerServiceImpl()
    {
        state = null;
        syncKey = new AtomicInteger(-1);
    }

    public GameStateManagerServiceImpl(Object state, AtomicInteger syncKey)
    {
        super();
        this.state = state;
        this.syncKey = syncKey;
    }

    public Object getState()
    {
        return state;
    }

    public Object getAndSetState(Object state)
    {
        this.state = state;
        return state;
    }

    public boolean compareAndSetState(Object key, Object state)
    {
        boolean syncKeySet = compareAndSetSyncKey(key);
        if(compareAndSetSyncKey(key))
        {
            this.state = state;
        }
        return syncKeySet;
    }

    public Object getSyncKey()
    {
        return syncKey.get();
    }

    public boolean compareAndSetSyncKey(Object key)
    {
        if (null == key || !(key instanceof Integer))
        {
            LOG.error("Invalid key provided: {}", key);
            return false;
        }

        Integer newKey = (Integer) key;
        return syncKey.compareAndSet(newKey, (++newKey));
    }

    public byte[] getSerializedByteArray()
    {
        return serializedBytes;
    }

    public void setSerializedByteArray(byte[] serializedBytes)
    {
        this.serializedBytes = serializedBytes;
    }



}