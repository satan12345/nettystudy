package com.able.nettystudy.com.netty.firstexample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

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
            //添加事件循环组
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

/**
 * 子处理器初始化类
 */
@Slf4j
class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //管道
        ChannelPipeline pipeline = ch.pipeline();
        //相当于增加拦截器
        //增加编码解码处理器
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast("mydef",new TestHttpServerHandler());
    }
}

/**
 * 自定义处理器
 */
@Slf4j
class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    /**
     * 读取客户端的请求 并向客户端返回响应的方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        log.info(msg.getClass().toString());

        if (msg instanceof HttpRequest) {
            System.out.println("=================");
            log.info("执行channelRead0");
            HttpRequest httpRequest= (HttpRequest) msg;
            log.info("remoteAddress={}",ctx.channel().remoteAddress());
            System.out.println("ctx.channel().id() = " + ctx.channel().id());
            log.info("method={}",httpRequest.method().name());
            String uri = httpRequest.uri();
            URI uri1=new URI(uri);
            if ("/favicon.ico".equals(uri1.getPath())) {
                log.info("请求favicon图标");
                System.out.println("=================");
                return;
            }
            //封装响应
            //1 封装响应的内容
            ByteBuf buf = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);
            //2 封装响应对象
            FullHttpResponse fullHttpResponse=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,buf);
            fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH,buf.readableBytes());
            ctx.writeAndFlush(fullHttpResponse);
            //ctx.channel().close();
            System.out.println("=================");
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
        super.channelRegistered(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelUnregistered");
        super.channelUnregistered(ctx);
    }
}
