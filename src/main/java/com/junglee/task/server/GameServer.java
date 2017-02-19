package com.junglee.task.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by vishwasourabh.sahay on 12/02/17.
 */
public class GameServer {


    public static void main(String[] args) throws Exception {

        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"Spring-beans.xml"});

       ServerManager serverManager= (ServerManager)context.getBean("serverManagerImpl");
       serverManager.startServers();

    }
}

