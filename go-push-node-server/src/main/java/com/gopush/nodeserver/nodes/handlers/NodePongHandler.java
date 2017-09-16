package com.gopush.nodeserver.nodes.handlers;

import com.gopush.common.Constants;
import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.protocol.node.NodeMessage;
import com.gopush.protocol.node.Pong;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/21 下午11:00
 * @VERSION：
 */

@Slf4j
@Component
public class NodePongHandler extends NodeBaseHandler implements INodeMessageHandler<Pong> {


    @Override
    public boolean support(NodeMessage message) {
        return message instanceof Pong;
    }

    @Override
    public void call(ChannelHandlerContext ctx, Pong message) {
        //可以做一些保活操作.其实就是确保活动
        //查询本地缓存是否存在该data center 的节点,有的话不做出来,没有的话加入新的节点
        saveLiveDc(ctx.channel());
        log.debug("receive pong, channel:{}, node:{}", ctx.channel(), ctx.channel().attr(Constants.CHANNEL_ATTR_DATACENTER).get());

    }
}
