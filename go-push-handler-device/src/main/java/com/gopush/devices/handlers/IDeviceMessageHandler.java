package com.gopush.devices.handlers;

import com.gopush.protocol.device.DeviceMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/11 上午11:43
 * @VERSION：
 */


public interface IDeviceMessageHandler<R> {

    /**
     * 根据各个handler 判断是不是各个handler对应处理的消息
     *
     * @param message 节点消息
     * @return 是否各个NodeMessage 子类的类型
     */
    boolean support(DeviceMessage message);

    /**
     * 各个消息处理句柄调用方法
     *
     * @param message 节点消息
     */
    void call(ChannelHandlerContext context, R message);
}
