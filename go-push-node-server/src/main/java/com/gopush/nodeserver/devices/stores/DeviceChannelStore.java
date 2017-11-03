package com.gopush.nodeserver.devices.stores;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * go-push
 *
 * @类功能说明：设备 - Channel 存储器
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/18 下午11:42
 * @VERSION：
 */

@Slf4j
@Component
public class DeviceChannelStore implements IDeviceChannelStore {

    //计数器
    private AtomicInteger counter = new AtomicInteger(0);


    //设备-channel列表
    private ConcurrentHashMap<String, Channel> deviceChannels = new ConcurrentHashMap<>();

    @Override
    public Channel getChannel(String device) {
        return deviceChannels.get(device);
    }

    @Override
    public void removeChannel(String device) {
        deviceChannels.remove(device);
        int count = counter.decrementAndGet();
        if (count < 0) {
            counter.set(0);
        }
    }

    @Override
    public void removeChannel(String device, Channel channel) {
        if (channel.equals(deviceChannels.get(device))) {
            removeChannel(device);
        }
    }

    @Override
    public void clear() {
        deviceChannels.clear();
        counter.set(0);
    }

    @Override
    public void addChannel(String device, Channel channel) {
        deviceChannels.put(device, channel);
        counter.incrementAndGet();
    }

    @Override
    public int count() {
        return counter.get();
    }
}
