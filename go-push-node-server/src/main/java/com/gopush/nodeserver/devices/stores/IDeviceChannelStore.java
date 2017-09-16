package com.gopush.nodeserver.devices.stores;

import io.netty.channel.Channel;

/**
 * go-push
 *
 * @类功能说明：设备-Channel 存储器
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/18 下午11:35
 * @VERSION：
 */
public interface IDeviceChannelStore {

    /**
     * 根据设备ID获取Channel
     *
     * @param device
     * @return
     */
    Channel getChannel(String device);

    /**
     * 根据设备ID删除 channel
     *
     * @param device
     */
    void removeChannel(String device);

    /**
     * 根据设备ID移除channel,对比 channel存不存在
     *
     * @param device
     * @param channel
     */
    void removeChannel(String device, Channel channel);


    /**
     * 清空channel
     */
    void clear();

    /**
     * 添加设备-channel
     *
     * @param device
     * @param channel
     */
    void addChannel(String device, Channel channel);


    /**
     * 设备-channel 计数
     *
     * @return
     */
    int count();

}
