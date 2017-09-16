package com.gopush.datacenter.infos.watchdog;

import com.gopush.common.utils.ip.IpUtils;
import com.gopush.datacenter.config.GoPushDataCenterConfig;
import com.gopush.datacenter.infos.watchdog.listener.event.DataCenterInfoEvent;
import com.gopush.datacenter.nodes.manager.NodeManager;
import com.gopush.datacenter.restfuls.loader.LoaderService;
import com.gopush.infos.datacenter.bo.DataCenterInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 上午8:28
 */

@Slf4j
@Component
public class DataCenterInfoWatchdog {

    @Autowired
    private GoPushDataCenterConfig goPushDataCenterConfig;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private NodeManager nodeManager;

    @Autowired
    private LoaderService loaderService;

    @Setter
    private int delay = 5000;

    private Timer timer;

    @PostConstruct
    public void init() {
        timer = new Timer("SendDataCenterInfo-Timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                applicationEventPublisher.publishEvent(DataCenterInfoEvent.builder()
                        .name(goPushDataCenterConfig.getName())
                        .dataCenterInfo(watch())
                        .build());
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
    public DataCenterInfo watch() {


        String internetIp = IpUtils.internetIp();
        String intranetIp = IpUtils.intranetIp();

        return DataCenterInfo.builder()
                .name(goPushDataCenterConfig.getName())
                .internetIp(StringUtils.isEmpty(internetIp) ? intranetIp : internetIp)
                .intranetIp(intranetIp)
                .nodeClientLoaderInfos(nodeManager.loaders())
                .restfulLoaderInfos(loaderService.restfulLoader())
                .build();
    }

}
