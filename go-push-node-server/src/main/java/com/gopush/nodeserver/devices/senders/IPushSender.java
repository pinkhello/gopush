package com.gopush.nodeserver.devices.senders;


import com.gopush.protocol.device.DeviceMessage;

/**
 * go-push
 *
 * @类功能说明：推送消息给设备
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/18 下午11:29
 * @VERSION：
 */
public interface IPushSender<T extends DeviceMessage> {

    /**
     * 发送消息给指定设备
     *
     * @param device
     * @param message
     */
    void send(String device, T message);
}
