package com.gopush.nodeserver.nodes.inbound;

import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.nodeserver.nodes.stores.IDataCenterChannelStore;
import com.gopush.protocol.node.NodeMessage;
import com.gopush.protocol.node.Ping;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/20 下午9:33
 * @VERSION：
 */

@Slf4j
@ChannelHandler.Sharable
@Component
public class NodeChannelInBoundHandler extends SimpleChannelInboundHandler<String> {

    private static String PING = Ping.builder().build().encode();

    @Autowired
    private IDataCenterChannelStore dataCenterChannelStore;


    @Autowired
    private List<INodeMessageHandler> nodeMessageHandlers;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        log.debug("channel:{}, message:{}", ctx.channel(), message);
        NodeMessage nodeMessage = NodeMessage.decode(message);
        if (!nodeMessageHandlers.isEmpty()) {
            nodeMessageHandlers.stream().forEach((handler) -> {
                try {
                    if (handler.support(nodeMessage)) {
                        handler.call(ctx, nodeMessage);
                    }
                } catch (Exception e) {
                    log.error("Exception error:{}", e);
                }
            });
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active, channel:{}", ctx.channel());
        dataCenterChannelStore.isDcChannelToSave(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel inactive, channel:{}", ctx.channel());
        dataCenterChannelStore.isDcChannelToRemove(ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception error:{}, channel:{}", cause, ctx.channel());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Channel channel = ctx.channel();
        dataCenterChannelStore.isDcChannelToSave(channel);
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
