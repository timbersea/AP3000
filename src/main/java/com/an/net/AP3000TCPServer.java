package com.an.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class AP3000TCPServer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AP3000TCPServer.class);
    private static final int READER_IDLE_SECONDS = 90;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final EventLoopGroup serviceGroup = new NioEventLoopGroup();
    private volatile Channel serverChannel;

    @Resource
    MessageHandler messageHandler;

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new IdleStateHandler(READER_IDLE_SECONDS, 0, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new AP3000Codec())
                                .addLast(serviceGroup, messageHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        log.info("tcp server bind on port {}", 8888);
        ChannelFuture f = b.bind(8888).sync();
        serverChannel = f.channel();
        serverChannel.closeFuture().sync();
    }

    @PreDestroy
    public void stop() {
        Channel channel = serverChannel;
        if (channel != null) {
            channel.close().syncUninterruptibly();
        }
        serviceGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("stop: boss/worker/service groups shutting down");
    }

    @Override
    public void run(String... args) {
        Thread thread = new Thread(() -> {
            try {
                start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.setName("netty-start");
        thread.start();
    }
}
