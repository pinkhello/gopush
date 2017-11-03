package com.gopush.nodeserver.dymic.register;

import com.alibaba.fastjson.JSON;
import com.gopush.common.constants.ZkGroupEnum;
import com.gopush.common.utils.zk.ZkUtils;
import com.gopush.common.utils.zk.listener.ZkStateListener;
import com.gopush.infos.nodeserver.bo.NodeServerInfo;
import com.gopush.nodeserver.config.GoPushNodeServerConfig;
import com.gopush.nodeserver.config.ZookeeperConfig;
import com.gopush.nodeserver.infos.watchdog.NodeServerInfoWatchdog;
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
 * @date 2017/9/10 下午10:42
 */
@Slf4j
@Component
public class NodeServerRegisterService {

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    @Autowired
    private GoPushNodeServerConfig goPushNodeServerConfig;

    @Autowired
    private NodeServerInfoWatchdog watchdog;

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
                        log.info("NodeServerRegister 链接zk成功");
                        registerNodeServer();

                    }

                    @Override
                    public void reconnectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("NodeServerRegister 重新链接zk成功");
                        registerNodeServer();
                    }

                    @Override
                    public void lostEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("NodeServerRegister 链接zk丢失");

                    }
                });


    }

    @PreDestroy
    public void destory() {
        zkUtils.destory();
    }


    /**
     * 提交最新的数据
     *
     * @param data
     */
    public void postNewData(NodeServerInfo data) {
        zkUtils.setNodeData(
                ZKPaths.makePath(ZkGroupEnum.NODE_SERVER.getValue(), goPushNodeServerConfig.getName()),
                JSON.toJSONString(data));
    }

    /**
     * 注册node-server服务
     */
    private void registerNodeServer() {

        if (!zkUtils.checkExists(ZkGroupEnum.NODE_SERVER.getValue())) {
            boolean flag;
            do {
                flag = zkUtils.createNode(ZkGroupEnum.NODE_SERVER.getValue(), null, CreateMode.PERSISTENT);
            } while (!flag);
        }
        registerNodeInfo();
    }

    private void registerNodeInfo() {
        zkUtils.createNode(
                ZKPaths.makePath(ZkGroupEnum.NODE_SERVER.getValue(), goPushNodeServerConfig.getName()),
                JSON.toJSONString(watchdog.watch()),
                CreateMode.EPHEMERAL);
    }


}
