package com.able.nettystudy.com.netty.firstexample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jipeng
 * @date 2019-04-22 19:02
 * @description
 */
@Slf4j
public class TestServer {

    public static void main(String[] args) throws Exception {
        //事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //简化服务启动的类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //添加时间循环组
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //定义子处理器
                    .childHandler(new TestServerInitializer());
            //端口绑定
            ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }


}

