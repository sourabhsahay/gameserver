package com.junglee.task.event;

import com.junglee.task.session.Session;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface EventContext
{
    Session getSession();
    void setSession(Session session);

    /**
     * Retrieves an object which is {@link #setAttachment(Object) attached} to
     * this context.
     *
     * @return {@code null} if no object was attached or
     *                      {@code null} was attached
     */
    Object getAttachment();

    /**
     * Attaches an object to this context to store a stateful information
     * specific to the {@link Event} which is associated with this
     * context.
     */
    void setAttachment(Object attachement);
}