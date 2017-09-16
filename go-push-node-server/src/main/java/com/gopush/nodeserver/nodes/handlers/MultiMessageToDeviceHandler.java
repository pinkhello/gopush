package com.gopush.nodeserver.nodes.handlers;

import com.gopush.common.constants.NodeMessageEnum;
import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.nodeserver.devices.senders.IPushSender;
import com.gopush.protocol.device.PushReq;
import com.gopush.protocol.node.MultiMessageToDeviceReq;
import com.gopush.protocol.node.MultiMessageToDeviceResp;
import com.gopush.protocol.node.NodeMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MultiMessageToDeviceHandler implements INodeMessageHandler<MultiMessageToDeviceReq> {

    @Autowired
    private IPushSender<PushReq> pushSender;

    @Override
    public boolean support(NodeMessage message) {
        return message instanceof MultiMessageToDeviceReq;
    }

    @Override
    public void call(ChannelHandlerContext ctx, MultiMessageToDeviceReq message) {
        //找寻到对应设备的channel 将消息全部推送给这个设备
        if (message != null) {
            pushSender.send(message.getDevice(), PushReq.builder().msgs(message.getMessages()).build());
            Channel channel = ctx.channel();
            channel.writeAndFlush(MultiMessageToDeviceResp.builder().result(NodeMessageEnum.OK.getCode()).build().encode());
        }
    }
}
