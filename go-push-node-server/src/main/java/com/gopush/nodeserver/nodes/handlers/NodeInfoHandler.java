package com.gopush.nodeserver.nodes.handlers;


import com.gopush.common.Constants;
import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.protocol.node.NodeInfoResp;
import com.gopush.protocol.node.NodeMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/20 下午11:00
 * @VERSION：
 */

@Slf4j
@Component
public class NodeInfoHandler extends NodeBaseHandler implements INodeMessageHandler<NodeInfoResp> {
    @Override
    public boolean support(NodeMessage message) {
        return message instanceof NodeInfoResp;
    }

    @Override
    public void call(ChannelHandlerContext ctx, NodeInfoResp message) {
        saveLiveDc(ctx.channel());
        log.debug("receive NodeInfoResp, channel:{}, node:{}", ctx.channel(), ctx.channel().attr(Constants.CHANNEL_ATTR_DATACENTER).get());

    }
}
