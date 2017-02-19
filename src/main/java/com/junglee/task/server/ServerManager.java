package com.junglee.task.server;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
public interface ServerManager
{
    public void startServers(int tcpPort, int udpPort) throws Exception;

    public void startServers() throws Exception;
    /**
     * Used to stop the server and manage cleanup of resources.
     *
     */
    public void stopServers() throws Exception;
}
