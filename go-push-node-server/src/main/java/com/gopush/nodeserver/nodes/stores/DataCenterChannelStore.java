package com.gopush.nodeserver.nodes.stores;

import com.gopush.common.Constants;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午1:10
 * @VERSION：
 */

@Slf4j
@Component
public class DataCenterChannelStore implements IDataCenterChannelStore {


    //计数器
    private AtomicInteger counter = new AtomicInteger(0);


    //DataCenter-channel列表
    private ConcurrentHashMap<String, Channel> dataCenterChannels = new ConcurrentHashMap<>();


    @Override
    public List<Channel> getAllChannels() {
        List<Channel> list = null;
        if (!dataCenterChannels.isEmpty()) {
            list = new ArrayList<>();
            list.addAll(dataCenterChannels.values());
        }
        return list;
    }

    @Override
    public boolean contains(String dcId) {
        return dataCenterChannels.containsKey(dcId);
    }

    @Override
    public Channel getChannel(String dcId) {
        return dataCenterChannels.get(dcId);
    }

    @Override
    public String getDcId(Channel channel) {
        final String[] dcId = {null};
        dataCenterChannels.forEach((String key, Channel target) -> {
            if (channel.equals(target)) {
                dcId[0] = key;
            }
        });
        return dcId[0];
    }


    @Override
    public void clear() {
        dataCenterChannels.clear();
        counter.set(0);
    }


    @Override
    public int count() {
        return counter.get();
    }


    @Override
    public void isDcChannelToSave(Channel channel) {
        if (!channel.hasAttr(Constants.CHANNEL_ATTR_DATACENTER)) {
            //添加相应的值
            String dcId = dataCenterId(channel);
            channel.attr(Constants.CHANNEL_ATTR_DATACENTER).set(dcId);
            if (!contains(dcId)) {
                addChannel(dcId, channel);
            }
        }
    }

    @Override
    public void isDcChannelToRemove(Channel channel) {
        String dcId = null;
        if (!channel.hasAttr(Constants.CHANNEL_ATTR_DATACENTER)) {
            dcId = dataCenterId(channel);
        } else {
            dcId = channel.attr(Constants.CHANNEL_ATTR_DATACENTER).get();
        }
        if (contains(dcId)) {
            removeChannel(dcId, channel);
        }
    }


    /**
     * 生产DC-ID
     *
     * @param channel
     * @return
     */
    private String dataCenterId(Channel channel) {
        return new StringBuilder()
                .append(channel.id())
                .append(channel.remoteAddress().toString())
                .toString();
    }


    /**
     * 根据DCID移除channel,对比 channel存不存在
     *
     * @param dcId
     * @param channel
     */
    private void removeChannel(String dcId, Channel channel) {
        if (channel.equals(dataCenterChannels.get(dcId))) {
            removeChannel(dcId);
        }
    }


    /**
     * 添加DC
     *
     * @param dcId
     * @param channel
     */
    private void addChannel(String dcId, Channel channel) {
        dataCenterChannels.put(dcId, channel);
        counter.incrementAndGet();
    }

    /**
     * 根据DCID删除 channel
     *
     * @param dcId
     */
    private void removeChannel(String dcId) {
        dataCenterChannels.remove(dcId);
        int count = counter.decrementAndGet();
        if (count < 0) {
            counter.set(0);
        }
    }

}



