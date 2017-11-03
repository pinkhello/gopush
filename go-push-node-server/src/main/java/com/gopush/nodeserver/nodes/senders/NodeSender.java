package com.gopush.nodeserver.nodes.senders;

import com.gopush.nodeserver.nodes.stores.IDataCenterChannelStore;
import com.gopush.protocol.node.NodeMessage;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午1:04
 * @VERSION：
 */

@Slf4j
@Component
public class NodeSender implements INodeSender<NodeMessage> {


    /**
     * 保证了发往DataCenter的消息不丢失
     */
    private Queue<InnerMessageInfo> failMessage = new ConcurrentLinkedQueue<>();

    @Autowired
    private IDataCenterChannelStore dataCenterChannelStore;

    @Setter
    private int delay = 2000;

    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void init() {


        scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("SendNodeMessage-Fail-Retry-schedule-pool-%d").daemon(true).build());
        scheduledExecutorService.scheduleAtFixedRate(() ->
                {
                    try {
                        if (failMessage.isEmpty()) {
                            return;
                        }
                        failMessage.stream().forEach((e) -> {
                            switch (e.getSt()) {
                                case ZD:
                                    send(e.getDcId(), e.getMessage());
                                    break;
                                case SJO:
                                    sendShuffle(e.getMessage());
                                    break;
                                case ALL:
                                    send(e.getMessage());
                                    break;
                                default:
                                    break;
                            }
                        });

                    } catch (Exception e) {
                        log.error("Exception error:{}", e);
                    }
                }
                , delay, delay, TimeUnit.MILLISECONDS);


    }

    @PreDestroy
    public void destory() {
        failMessage.clear();

    }


    @Override
    public void send(String dcId, NodeMessage message) {
        if (dataCenterChannelStore.count() > 0) {
            if (dataCenterChannelStore.contains(dcId)) {
                Channel channel = dataCenterChannelStore.getChannel(dcId);
                channel.writeAndFlush(message.encode()).addListener((future) -> {
                    if (!future.isSuccess()) {
                        dataCenterChannelStore.isDcChannelToRemove(channel);
                        addFailMessage(message, SendType.SJO, null);
                    } else {
                        //从哪边移除
                        removeFailMessage(message, SendType.ZD, dcId);
                    }
                });
            } else {
                addFailMessage(message, SendType.SJO, null);
            }
        } else {
            log.warn("send dcid can not find data center, retry later!");
            addFailMessage(message, SendType.SJO, null);
        }
    }


    @Override
    public void sendShuffle(NodeMessage message) {
        if (dataCenterChannelStore.count() > 0) {
            List<Channel> list = new ArrayList<>(dataCenterChannelStore.getAllChannels());
            Collections.shuffle(list);
            Channel channel = list.get(0);
            channel.writeAndFlush(message.encode()).addListener((future) -> {
                if (!future.isSuccess()) {
                    dataCenterChannelStore.isDcChannelToRemove(channel);
                    addFailMessage(message, SendType.SJO, null);
                } else {
                    //从哪边移除
                    removeFailMessage(message, SendType.SJO, null);
                }
            });
        } else {
            log.warn(" sendShuffle can not find data center, retry later!");
            addFailMessage(message, SendType.SJO, null);
        }
    }

    @Override
    public void send(NodeMessage message) {
        if (dataCenterChannelStore.count() > 0) {
            List<Channel> list = new ArrayList<>(dataCenterChannelStore.getAllChannels());
            for (Channel channel : list) {
                channel.writeAndFlush(message.encode()).addListener((future) -> {
                    if (!future.isSuccess()) {
                        String dcId = dataCenterChannelStore.getDcId(channel);
                        if (dcId != null) {
                            addFailMessage(message, SendType.ZD, dcId);
                        }
                        dataCenterChannelStore.isDcChannelToRemove(channel);
                    }
                });
            }
            removeFailMessage(message, SendType.ALL, null);
        } else {
            log.warn("send can not find data center, retry later!");
            addFailMessage(message, SendType.ALL, null);
        }
    }


    private void removeFailMessage(NodeMessage message, SendType st, String dcId) {
        InnerMessageInfo.InnerMessageInfoBuilder builder = InnerMessageInfo
                .builder()
                .message(message)
                .st(st);
        switch (st) {
            case ZD:
                builder.dcId(dcId);
                break;
            default:
                break;
        }
        InnerMessageInfo info = builder.build();
        if (failMessage.contains(info)) {
            failMessage.remove(info);
        }
    }

    private void addFailMessage(NodeMessage message, SendType st, String dcId) {

        InnerMessageInfo.InnerMessageInfoBuilder builder = InnerMessageInfo
                .builder()
                .message(message)
                .st(st);
        switch (st) {
            case ZD:
                builder.dcId(dcId);
                break;
            default:
                break;
        }
        InnerMessageInfo info = builder.build();
        if (!failMessage.contains(info)) {
            failMessage.add(info);
        }
    }


    enum SendType {
        ZD,
        SJO,
        ALL
    }


}


@Builder
@Data
@EqualsAndHashCode
class InnerMessageInfo {
    private NodeSender.SendType st;
    private NodeMessage message;
    private String dcId;
}