package com.gopush.datacenter.nodes.handlers;

import com.gopush.common.constants.NodeMessageEnum;
import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.protocol.node.DeviceDockedReq;
import com.gopush.protocol.node.DeviceDockedResp;
import com.gopush.protocol.node.NodeMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/21 下午11:05
 * @VERSION：
 */

@Slf4j
@Component
public class DeviceDockedHandler implements INodeMessageHandler<DeviceDockedReq> {
    @Override
    public boolean support(NodeMessage message) {
        return message instanceof DeviceDockedReq;
    }

    @Override
    public void call(ChannelHandlerContext ctx, DeviceDockedReq message) {
        Channel channel = ctx.channel();
        channel.writeAndFlush(DeviceDockedResp.builder().result(NodeMessageEnum.OK.getCode()).build().encode());
        log.debug("receive DeviceDockedReq, channel:{}", channel);
    }
}
