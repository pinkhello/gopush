package com.gopush.datacenter.nodes.manager;

import com.gopush.infos.datacenter.bo.NodeClientLoaderInfo;
import com.gopush.nodes.handlers.INodeMessageHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/24 下午3:33
 * @VERSION：
 */

@Slf4j
@Component
public class NodeManager {

    private EventLoopGroup group = new NioEventLoopGroup();

    private Map<String, Node> nodeChannelPool = new ConcurrentHashMap<>();

    @Autowired
    private List<INodeMessageHandler> nodeMessageHandlers;

    @PreDestroy
    public void destory() {
        nodeChannelPool.forEach((k, node) -> node.destroy());
        nodeChannelPool.clear();
        nodeChannelPool = null;
        group.shutdownGracefully();
    }


    public void clear() {
        nodeChannelPool.forEach((k, node) -> node.destroy());
        nodeChannelPool.clear();

    }


    public void remove(String nodeName) {
//        log.info("node remove---------{}",nodeName);
        if (nodeChannelPool.containsKey(nodeName)) {
            nodeChannelPool.get(nodeName).destroy();
            nodeChannelPool.remove(nodeName);
        }
    }

    public void put(String nodeName,
                    String intranetIp, int nodePort,
                    String internetIp, int devicePort) {
        remove(nodeName);
//        log.info("node add---------",nodeName);
        Node node = new Node(nodeName + "-client", intranetIp, nodePort, internetIp, devicePort, group, nodeMessageHandlers);
        node.init();
        nodeChannelPool.put(nodeName, node);
//        log.info("{}", JSON.toJSONString(nodeChannelPool));
    }

    public List<NodeClientLoaderInfo> loaders() {
        return nodeChannelPool.values().stream().map(e -> NodeClientLoaderInfo.builder().name(e.getName()).receiveCounter(e.receiveCounter()).sendCounter(e.sendCounter()).build()).collect(Collectors.toList());
    }

}
