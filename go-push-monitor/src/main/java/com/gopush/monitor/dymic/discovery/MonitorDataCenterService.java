package com.gopush.monitor.dymic.discovery;

import com.alibaba.fastjson.JSON;
import com.gopush.common.constants.ZkGroupEnum;
import com.gopush.common.utils.zk.ZkUtils;
import com.gopush.common.utils.zk.listener.ZkStateListener;
import com.gopush.infos.datacenter.bo.DataCenterInfo;
import com.gopush.monitor.config.ZookeeperConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.state.ConnectionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 下午3:50
 */

@Slf4j
@Component
public class MonitorDataCenterService {

    //缓存的本地服务列表
    private Map<String, DataCenterInfo> monitorDataCenterPool = new ConcurrentHashMap<>();


    @Autowired
    private ZookeeperConfig zookeeperConfig;

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
                zookeeperConfig.getListenNamespaceDataCenter(),
                new ZkStateListener() {
                    @Override
                    public void connectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("MonitorDataCenter 链接zk成功");
                        initDataCenterPool();

                    }

                    @Override
                    public void reconnectedEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("MonitorDataCenter 重新链接zk成功");
                        initDataCenterPool();
                    }

                    @Override
                    public void lostEvent(CuratorFramework curator, ConnectionState state) {
                        log.info("MonitorDataCenter 链接zk丢失");
                        monitorDataCenterPool.clear();
                    }
                });

        listenDataCenter();
    }

    @PreDestroy
    public void destory() {
        monitorDataCenterPool.clear();
        zkUtils.destory();
    }


    public List<DataCenterInfo> dataCenterLoader() {
        return monitorDataCenterPool.values().stream().collect(Collectors.toList());
    }

    private void initDataCenterPool() {
        monitorDataCenterPool.clear();
        Map<String, String> datas = zkUtils.readTargetChildsData(ZkGroupEnum.DATA_CENTER.getValue());
        if (datas != null) {
            datas.forEach((k, v) -> monitorDataCenterPool.put(k, JSON.parseObject(v, DataCenterInfo.class)));
        }
    }

    /**
     * 设置监听发生更新，更新缓存数据，发生新增，删除，更新
     */
    private void listenDataCenter() {
        zkUtils.listenerPathChildrenCache(ZkGroupEnum.DATA_CENTER.getValue(), ((client, event) -> {
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
        DataCenterInfo data = toDataCenterInfo(event);
        log.debug(" Monitor data center event update! key:{}, data:{}", key, data);
        if (monitorDataCenterPool.containsKey(key)) {
            monitorDataCenterPool.put(key, data);
        }
    }

    private void removeEvent(PathChildrenCacheEvent event) {
        String key = toKey(event);
        DataCenterInfo data = toDataCenterInfo(event);
        log.debug(" Monitor data center event remove! key:{}, data:{}", key, data);
        if (monitorDataCenterPool.containsKey(key)) {
            monitorDataCenterPool.remove(key);
        }

    }

    private void addEvent(PathChildrenCacheEvent event) {
        String key = toKey(event);
        DataCenterInfo data = toDataCenterInfo(event);
        log.debug(" Monitor data center event add! key:{}, data:{}", key, data);
        if (!monitorDataCenterPool.containsKey(key)) {
            //开启node,加入到管理器
            monitorDataCenterPool.put(key, data);
        } else {
            log.error(" Monitor data center already! {},{}", key, data);
        }
    }


    private String toKey(PathChildrenCacheEvent event) {
        String path = event.getData().getPath();
        return path.substring(path.lastIndexOf("/")).replaceAll("/", "");
    }

    private DataCenterInfo toDataCenterInfo(PathChildrenCacheEvent event) {
        return JSON.parseObject(event.getData().getData(), DataCenterInfo.class);
    }


}
