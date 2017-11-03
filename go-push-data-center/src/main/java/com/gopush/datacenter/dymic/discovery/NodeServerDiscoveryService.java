package com.gopush.datacenter.dymic.discovery;

import com.alibaba.fastjson.JSON;
import com.gopush.common.constants.ZkGroupEnum;
import com.gopush.common.utils.zk.ZkUtils;
import com.gopush.common.utils.zk.listener.ZkStateListener;
import com.gopush.datacenter.config.ZookeeperConfig;
import com.gopush.datacenter.nodes.manager.NodeManager;
import com.gopush.infos.nodeserver.bo.NodeServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/12 上午3:13
 */

@Slf4j
@Component
public class NodeServerDiscoveryService {

    /**
     * 缓存的本地服务列表
     */
    private Map<String, NodeServerInfo> nodeServerPool = new ConcurrentHashMap<>();

    @Autowired
    private ZookeeperConfig zookeeperConfig;

    @Autowired
    private NodeManager nodeManager;


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
                        log.info("NodeServerDiscovery 链接zk成功");
                        initNodeServerDiscovery();

                    }

                    @Override
                    public void reconnectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("NodeServerDiscovery 重新链接zk成功");
                        initNodeServerDiscovery();
                    }

                    @Override
                    public void lostEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("NodeServerDiscovery 链接zk丢失");
                        nodeServerPool.clear();
                        nodeManager.clear();
                    }
                });

        listenNodeServerDiscovery();
    }

    @PreDestroy
    public void destory() {
        nodeServerPool.clear();
        zkUtils.destory();
    }

    /**
     * 获取当前现在的Node -Server 服务列表
     *
     * @return
     */
    public Map<String, NodeServerInfo> nodeServerPool() {
        return new HashMap<>(nodeServerPool);
    }

    /**
     * 初始化node-server列表
     */
    private void initNodeServerDiscovery() {
        nodeServerPool.clear();
        Map<String, String> datas = zkUtils.readTargetChildsData(ZkGroupEnum.NODE_SERVER.getValue());
        if (datas != null) {
            datas.forEach((k, v) -> nodeServerPool.put(k, JSON.parseObject(v, NodeServerInfo.class)));
        }
        nodeServerPool().forEach((k, v) -> nodeManager.put(k, v.getIntranetIp(), v.getNodePort(), v.getInternetIp(), v.getDevicePort()));

    }

    /**
     * 设置监听发生更新，更新缓存数据，发生新增，删除，更新
     */
    private void listenNodeServerDiscovery() {
        zkUtils.listenerPathChildrenCache(ZkGroupEnum.NODE_SERVER.getValue(), ((client, event) -> {
            switch (event.getType()) {
                case CHILD_ADDED:
                    addEvent(event);
                    break;
                case CHILD_REMOVED:
                    removeEvent(event);
                    break;
                case CHILD_UPDATED:
                    updateEvent(event);
                    break;
                default:
                    break;
            }
        }));
    }

    private void updateEvent(PathChildrenCacheEvent event) {
        String key = toKey(event);
        NodeServerInfo data = toNodeServerInfo(event);
        log.debug("node event update! key:{}, data:{}", key, data);
        //只需要更新缓存数据就可以了
        if (nodeServerPool.containsKey(key)) {
            nodeServerPool.put(key, data);
        }
    }

    private void removeEvent(PathChildrenCacheEvent event) {
        String key = toKey(event);
        NodeServerInfo data = toNodeServerInfo(event);
        log.info("node event remove! key:{}, data:{}", key, data);
        if (nodeServerPool.containsKey(key)) {
            //检测Node是否还存在，存在的话移除该Node
            nodeManager.remove(key);
            nodeServerPool.remove(key);
        }

    }

    private void addEvent(PathChildrenCacheEvent event) {
        String key = toKey(event);
        NodeServerInfo data = toNodeServerInfo(event);
        log.info("node event add! key:{}, data:{}", key, data);
        if (!nodeServerPool.containsKey(key)) {
            //开启node,加入到管理器
            nodeManager.put(key, data.getIntranetIp(), data.getNodePort(), data.getInternetIp(), data.getDevicePort());
            nodeServerPool.put(key, data);
        } else {
            log.error("node already! {},{}", key, data);
        }
    }


    private String toKey(PathChildrenCacheEvent event) {
        String path = event.getData().getPath();
        return path.substring(path.lastIndexOf("/")).replaceAll("/", "");
    }

    private NodeServerInfo toNodeServerInfo(PathChildrenCacheEvent event) {
        return JSON.parseObject(event.getData().getData(), NodeServerInfo.class);
    }

}
