package com.junglee.task.server;

import com.junglee.task.session.Session;

import java.net.InetSocketAddress;

/**
 * Created by vishwasourabh.sahay on 17/02/17.
 */
public interface Server {

    public enum TRANSMISSION_PROTOCOL  {
        TCP,UDP;
    }

    TRANSMISSION_PROTOCOL getTransmissionProtocol();

    void startServer() throws Exception;

    void startServer(int port) throws Exception;

    void startServer(InetSocketAddress socketAddress) throws Exception;

    void stopServer() throws Exception;

    InetSocketAddress getSocketAddress();

    Session getSession();

    void setSession(Session session);
}
