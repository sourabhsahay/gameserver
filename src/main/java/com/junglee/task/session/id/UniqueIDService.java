package com.junglee.task.session.id;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public interface UniqueIDService
{
    public Object generate() throws  Exception;

    public Object generateFor(@SuppressWarnings("rawtypes") Class klass);
}

