package com.able.nettystudy.com.netty.thirdexample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jipeng
 * @date 2019-04-23 19:07
 * @description
 */
public class MyChatServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind(9898).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
 class  ServerChannelInitializer extends ChannelInitializer<SocketChannel>{

     @Override
     protected void initChannel(SocketChannel ch) throws Exception {
         ChannelPipeline pipeline = ch.pipeline();
         pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
         pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
         pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
         pipeline.addLast(new MyChatChannelHandler());

     }
 }
 class Data{
        public static final ChannelGroup CHANNELS=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
 }
 @Slf4j
 class MyChatChannelHandler extends SimpleChannelInboundHandler<String>{


     /**
      * 链接建立
      * @param ctx
      * @throws Exception
      */
     @Override
     public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
         Channel channel = ctx.channel();
         log.info("【服务器】-"+channel.remoteAddress()+"加入\n");
         Data.CHANNELS.writeAndFlush("【服务器】-"+channel.remoteAddress()+"加入\n");
         Data.CHANNELS.add(channel);

     }

     /**
      * 连接断开
      * @param ctx
      * @throws Exception
      */
     @Override
     public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
         Channel channel = ctx.channel();
         Data.CHANNELS.remove(channel);
         Data.CHANNELS.writeAndFlush("【服务器】-"+channel.remoteAddress()+"离开\n");
     }

     /**
      * 上线
      * @param ctx
      * @throws Exception
      */
     @Override
     public void channelActive(ChannelHandlerContext ctx) throws Exception {
         log.info("【服务端】-"+ctx.channel().remoteAddress()+"上线\n");
     }

     /**
      * 下限
      * @param ctx
      * @throws Exception
      */
     @Override
     public void channelInactive(ChannelHandlerContext ctx) throws Exception {
         log.info("【服务端】-"+ctx.channel().remoteAddress()+"下线\n");
     }

     @Override
     protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
         Channel channel = ctx.channel();
         Data.CHANNELS.forEach(x->{
             if (!x.remoteAddress().equals(channel.remoteAddress())) {
                 x.writeAndFlush(channel.remoteAddress()+"发送的消息:"+msg+"\n");
             }else {
                 x.writeAndFlush("【自己】"+msg+"\n");
             }
         });
     }

     @Override
     public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
         cause.printStackTrace();
         ctx.close();
     }

 }

