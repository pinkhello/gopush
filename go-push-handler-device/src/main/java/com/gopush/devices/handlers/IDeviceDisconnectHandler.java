package com.gopush.devices.handlers;

import io.netty.channel.Channel;

/**
 * go-push
 *
 * @类功能说明：设备断连处理接口
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午12:20
 * @VERSION：
 */
public interface IDeviceDisconnectHandler {


    /**
     * channel关闭,触发
     *
     * @param channel
     */
    void channelClosed(Channel channel);

}
