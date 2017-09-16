package com.gopush.datacenter.nodes.handlers;

import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.protocol.node.NodeMessage;
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
public class PongHandler implements INodeMessageHandler<Pong> {
    @Override
    public boolean support(NodeMessage message) {
        return message instanceof Pong;
    }

    @Override
    public void call(ChannelHandlerContext ctx, Pong message) {
        Channel channel = ctx.channel();


        log.debug("receive pong! channel:{}", channel);
    }
}
