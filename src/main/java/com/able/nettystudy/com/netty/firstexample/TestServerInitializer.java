package com.able.nettystudy.com.netty.firstexample;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * @author jipeng
 * @date 2019-04-23 14:44
 * @description
 */
@Slf4j
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast("mydef",new TestHttpServerHandler());
    }
    class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            log.info(msg.getClass().toString());

            if (msg instanceof HttpRequest) {
                System.out.println("=================");
                log.info("执行channelRead0");
                HttpRequest httpRequest= (HttpRequest) msg;
                log.info("remoteAddress={}",ctx.channel().remoteAddress());
                log.info("method={}",httpRequest.method().name());
                String uri = httpRequest.uri();
                URI uri1=new URI(uri);
                if ("/favicon.ico".equals(uri1.getPath())) {
                    log.info("请求favicon图标");
                    System.out.println("=================");
                    return;
                }
                ByteBuf buf = Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);
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
}
