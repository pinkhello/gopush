package com.gopush.datacenter.dymic.register;

import com.alibaba.fastjson.JSON;
import com.gopush.common.constants.ZkGroupEnum;
import com.gopush.common.utils.zk.ZkUtils;
import com.gopush.common.utils.zk.listener.ZkStateListener;
import com.gopush.datacenter.config.GoPushDataCenterConfig;
import com.gopush.datacenter.config.ZookeeperConfig;
import com.gopush.datacenter.infos.watchdog.DataCenterInfoWatchdog;
import com.gopush.infos.datacenter.bo.DataCenterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/12 下午2:20
 */

@Slf4j
@Component
public class DataCenterRegisterService {

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    @Autowired
    private GoPushDataCenterConfig goPushDataCenterConfig;

    @Autowired
    private DataCenterInfoWatchdog watchdog;

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
                zookeeperConfig.getNamespace(),
                new ZkStateListener() {
                    @Override
                    public void connectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("DataCenterRegister 链接zk成功");
                        registerDataCenter();

                    }

                    @Override
                    public void reconnectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("DataCenterRegister 重新链接zk成功");
                        registerDataCenter();
                    }

                    @Override
                    public void lostEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("DataCenterRegister 链接zk丢失");
                    }
                });


    }

    @PreDestroy
    public void destory() {
        zkUtils.destory();
    }


    public void postNewData(DataCenterInfo data) {
        zkUtils.setNodeData(
                ZKPaths.makePath(ZkGroupEnum.DATA_CENTER.getValue(), goPushDataCenterConfig.getName()),
                JSON.toJSONString(data));
    }

    /**
     * 注册datacenter服务
     */
    private void registerDataCenter() {

        if (!zkUtils.checkExists(ZkGroupEnum.DATA_CENTER.getValue())) {
            boolean flag;
            do {
                flag = zkUtils.createNode(ZkGroupEnum.DATA_CENTER.getValue(), null, CreateMode.PERSISTENT);
            } while (!flag);
        }
        registerDataCenterInfo();
    }

    private void registerDataCenterInfo() {
        zkUtils.createNode(
                ZKPaths.makePath(ZkGroupEnum.DATA_CENTER.getValue(), goPushDataCenterConfig.getName()),
                JSON.toJSONString(watchdog.watch()),
                CreateMode.EPHEMERAL);
    }

}
