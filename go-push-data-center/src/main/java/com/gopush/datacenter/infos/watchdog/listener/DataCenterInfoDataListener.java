package com.gopush.datacenter.infos.watchdog.listener;

import com.gopush.datacenter.dymic.register.DataCenterRegisterService;
import com.gopush.datacenter.infos.watchdog.listener.event.DataCenterInfoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 上午8:39
 */
@Slf4j
@Component
public class DataCenterInfoDataListener {
    @Autowired
    private DataCenterRegisterService dataCenterRegisterService;

    @Async
    @EventListener(condition = "#event.dataCenterInfo != null")
    public void postDataToZk(DataCenterInfoEvent event) {
        dataCenterRegisterService.postNewData(event.getDataCenterInfo());
    }
}
