package com.gopush.nodeserver.infos.watchdog;

import com.gopush.common.utils.ip.IpUtils;
import com.gopush.infos.nodeserver.bo.NodeServerInfo;
import com.gopush.nodeserver.config.GoPushNodeServerConfig;
import com.gopush.nodeserver.devices.BatchProcessor;
import com.gopush.nodeserver.devices.stores.IDeviceChannelStore;
import com.gopush.infos.nodeserver.bo.NodeLoaderInfo;
import com.gopush.nodeserver.infos.watchdog.listener.event.NodeServerInfoEvent;
import com.gopush.nodeserver.nodes.senders.INodeSender;
import com.gopush.nodeserver.nodes.stores.IDataCenterChannelStore;
import com.gopush.protocol.node.NodeInfoReq;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/10 下午2:16
 */
@Slf4j
@Component
public class NodeServerInfoWatchdog {

    @Autowired
    private GoPushNodeServerConfig goPushNodeServerConfig;

    @Autowired
    private INodeSender nodeSender;

    @Autowired
    private IDeviceChannelStore deviceChannelStore;

    @Autowired
    private IDataCenterChannelStore dataCenterChannelStore;

    @Autowired
    private List<BatchProcessor> deviceMessageHandlers;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Setter
    private int delay = 5000;

    private Timer timer;

    @PostConstruct
    public void init() {
        timer = new Timer("SendNodeServerInfo-Timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //将负载加载到ZK中
                if (!CollectionUtils.isEmpty(dataCenterChannelStore.getAllChannels())){
                    dataCenterChannelStore.getAllChannels().stream().forEach(e->{
                        log.info("channel id:{}, {}",e.id(),e);
                    });
                }
                applicationEventPublisher.publishEvent(
                        NodeServerInfoEvent.builder()
                                .name(goPushNodeServerConfig.getName())
                                .nodeServerInfo(watch())
                                .build());
//                写入zk 其实不需要发送 NodeInfoReq
                nodeSender.send(NodeInfoReq.builder().build());
            }
        }, delay, delay);
    }


    @PreDestroy
    public void destory() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 获取系统负载信息
     *
     * @return
     */
    public NodeServerInfo watch() {
        String internetIp = IpUtils.internetIp();
        String intranetIp = IpUtils.intranetIp();

        return NodeServerInfo.builder()
                .name(goPushNodeServerConfig.getName())
                .internetIp(StringUtils.isEmpty(internetIp) ? intranetIp : internetIp)
                .intranetIp(intranetIp)
                .devicePort(goPushNodeServerConfig.getDevicePort())
                .nodePort(goPushNodeServerConfig.getNodePort())
                .nodeLoaderInfo(NodeLoaderInfo.builder()
                        .onlineDcCounter(dataCenterChannelStore.count())
                        .onlineDeviceCounter(deviceChannelStore.count())
                        .handlerInfos(deviceMessageHandlers.stream().map(BatchProcessor::getHandlerInfo).collect(Collectors.toList()))
                        .build()
                )
                .build();
    }
}
