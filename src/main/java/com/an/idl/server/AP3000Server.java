package com.an.idl.server;

import com.an.idl.ap3000.AP3000Service;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class AP3000Server {
    private static final Logger log = LoggerFactory.getLogger(AP3000Server.class);
    TServer server;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                AP3000ServiceImpl ap3000Server = new AP3000ServiceImpl();
                AP3000Service.Processor<AP3000ServiceImpl> ap3000ServerProcessor =
                        new AP3000Service.Processor<>(ap3000Server);

                TServerTransport serverTransport = new TServerSocket(9090);
                server =
                        new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(ap3000ServerProcessor));
                // Use this for a multithreaded server
                server.serve();
            } catch (Exception e) {
            }
        }).start();
        log.info("Starting the simple server...");
    }

    @PreDestroy
    public void destroy() {
        server.stop();
    }
}
