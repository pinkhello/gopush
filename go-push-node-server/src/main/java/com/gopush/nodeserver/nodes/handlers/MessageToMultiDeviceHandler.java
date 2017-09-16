package com.gopush.nodeserver.nodes.handlers;

import com.gopush.common.constants.NodeMessageEnum;
import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.nodeserver.devices.senders.IPushSender;
import com.gopush.protocol.device.PushReq;
import com.gopush.protocol.node.MessageToMultiDeviceReq;
import com.gopush.protocol.node.MessageToMultiDeviceResp;
import com.gopush.protocol.node.NodeMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


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
public class MessageToMultiDeviceHandler implements INodeMessageHandler<MessageToMultiDeviceReq> {

    @Autowired
    private IPushSender pushSender;

    @Override
    public boolean support(NodeMessage message) {
        return message instanceof MessageToMultiDeviceReq;
    }

    @Override
    public void call(ChannelHandlerContext ctx, MessageToMultiDeviceReq message) {
        //找寻到对应设备的channel 将消息全部推送给这个设备
        if (message != null) {
            if (CollectionUtils.isNotEmpty(message.getDevices())) {
                List<String> devcies = message.getDevices();
                devcies.stream().forEach((e) -> {
                    List<String> msgList = new ArrayList<>();
                    PushReq pushReq = PushReq.builder().msgs(msgList).build();
                    pushSender.send(e, pushReq);
                });

                Channel channel = ctx.channel();
                channel.writeAndFlush(MessageToMultiDeviceResp.builder().result(NodeMessageEnum.OK.getCode()).build().encode());
            }
        }


    }
}
