package com.gopush.datacenter.nodes.handlers;

import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.protocol.node.NodeMessage;
import com.gopush.protocol.node.Ping;
import com.gopush.protocol.node.Pong;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/21
 * @VERSION：
 */
@Slf4j
@Component
public class PingHandler implements INodeMessageHandler<Ping> {

    private static final String PONG = Pong.builder().build().encode();

    @Override
    public boolean support(NodeMessage message) {
        return message instanceof Ping;
    }

    @Override
    public void call(ChannelHandlerContext ctx, Ping message) {
        Channel channel = ctx.channel();
        channel.writeAndFlush(PONG);
        log.debug("receive ping,channel:{}", channel);
    }
}
