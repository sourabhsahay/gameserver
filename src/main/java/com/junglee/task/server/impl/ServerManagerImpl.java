package com.junglee.task.server.impl;

import com.junglee.task.server.Server;
import com.junglee.task.server.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vishwasourabh.sahay on 18/02/17.
 */
@Component
public class ServerManagerImpl implements ServerManager,ApplicationContextAware
{
    private Set<AbstractServer> servers;
    private static final Logger LOG = LoggerFactory.getLogger(ServerManagerImpl.class);

    ApplicationContext context ;

    public ServerManagerImpl()
    {
        servers = new HashSet<AbstractServer>();
    }

    public void startServers(int tcpPort, int udpPort) throws Exception
    {

        if(tcpPort > 0)
        {
            AbstractServer tcpServer = (AbstractServer)context.getBean("tCPServer");
            tcpServer.startServer(tcpPort);
            servers.add(tcpServer);
        }

        if(udpPort > 0)
        {
            AbstractServer udpServer = (AbstractServer)context.getBean("uDPServer");
            udpServer.startServer(udpPort);
            servers.add(udpServer);
        }

    }

    public void startServers() throws Exception
    {
        context.getBeansOfType(Server.class);
        AbstractServer tcpServer = (AbstractServer)context.getBean("TCPServer");
        tcpServer.startServer();
        servers.add(tcpServer);
        AbstractServer udpServer = (AbstractServer)context.getBean("UDPServer");
        udpServer.startServer();
        servers.add(udpServer);
    }

    public void stopServers() throws Exception
    {
        for(AbstractServer nettyServer: servers){
            try
            {
                nettyServer.stopServer();
            }
            catch (Exception e)
            {
                LOG.error("Unable to stop server {} due to error {}", nettyServer,e);
                throw e;
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        this.context = applicationContext;
    }


}
