package com.able.nettystudy.com.netty.forthexample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @param
 * @author jipeng
 * @date 2019-04-28 16:40
 */
public class HeartBeatServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    //handler针对bossGroup
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .channel(NioServerSocketChannel.class)
                    //childHandler针对workerGroup
                    .childHandler(new ServerChannelInitializer());

            ChannelFuture channelFuture = serverBootstrap.bind(9800).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
    class ServerChannelInitializer extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            //空闲状态监测的处理器
            pipeline.addLast(new IdleStateHandler(5,7,10, TimeUnit.SECONDS));
            pipeline.addLast(new ServerChannelHandler());
        }
    }
    @Slf4j
    class ServerChannelHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent idleStateEvent= (IdleStateEvent) evt;
                IdleState state = idleStateEvent.state();
                String eventType=null;
                if (IdleState.READER_IDLE.equals(state)) {
                    eventType="读空闲";
                }
                if (IdleState.WRITER_IDLE.equals(state)) {
                    eventType="写空闲";
                }
                if (IdleState.ALL_IDLE.equals(state)) {
                    eventType="读写空闲";
                }
                log.info(ctx.channel().remoteAddress()+"超时事件:"+eventType);
                ctx.channel().close();
            }
        }
    }


