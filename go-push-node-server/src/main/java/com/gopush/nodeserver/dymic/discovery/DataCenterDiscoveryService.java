package com.gopush.nodeserver.dymic.discovery;

import com.gopush.common.utils.zk.ZkUtils;
import com.gopush.common.utils.zk.listener.ZkStateListener;
import com.gopush.nodeserver.config.GoPushNodeServerConfig;
import com.gopush.nodeserver.config.ZookeeperConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/12 上午3:13
 */

@Slf4j
@Component
public class DataCenterDiscoveryService {

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    @Autowired
    private GoPushNodeServerConfig goPushNodeServerConfig;

    private ZkUtils zkUtils;

    @PostConstruct
    public void init() {
        zkUtils = new ZkUtils();
        zkUtils.init(
                zookeeperConfig.getServers(),
                zookeeperConfig.getConnectionTimeout(),
                zookeeperConfig.getSessionTimeout(),
                zookeeperConfig.getMaxRetries(),
                zookeeperConfig.getRetriesSleepTime(),
                zookeeperConfig.getListenNamespace(),
                new ZkStateListener() {
                    @Override
                    public void connectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("DataCenterDiscovery 链接zk成功");
                    }

                    @Override
                    public void reconnectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("DataCenterDiscovery 重新链接zk成功");
                    }

                    @Override
                    public void lostEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("DataCenterDiscovery 链接zk丢失");
                    }
                });

    }

    @PreDestroy
    public void destory() {
        zkUtils.destory();
    }


}
