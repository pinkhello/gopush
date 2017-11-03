package com.gopush.datacenter.nodes.inbound;

import com.gopush.datacenter.nodes.manager.Node;
import com.gopush.protocol.node.NodeMessage;
import com.gopush.protocol.node.Ping;
import com.gopush.protocol.node.Pong;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/24 上午12:23
 * @VERSION：
 */

@Slf4j
@Data
@ChannelHandler.Sharable
public class NodeChannelInBoundHandler extends SimpleChannelInboundHandler<String> {

    private static String PING = Ping.builder().build().encode();

    /**
     * 对应的Node节点
     */
    private Node node;

    public NodeChannelInBoundHandler(Node node) {
        this.node = node;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active, channel:{}", ctx.channel());
        node.active();
        //有发送失败的补发
        node.retrySendFail();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel inactive, channel:{}", ctx.channel());
        node.inactive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        NodeMessage nodeMessage = NodeMessage.decode(message);
        //是心跳的，设置节点存活
        if (nodeMessage instanceof Ping || nodeMessage instanceof Pong) {
            node.active();
        }
        node.handle(ctx, nodeMessage);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        node.reconnect(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception error:{}, channel:{}", cause, ctx.channel());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel channel = ctx.channel();
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                //发送心跳
                channel.writeAndFlush(PING);
            }
            if (event.state() == IdleState.READER_IDLE) {
                //发送心跳
                channel.writeAndFlush(PING);
            }
            if (event.state() == IdleState.WRITER_IDLE) {
                channel.writeAndFlush(PING);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
