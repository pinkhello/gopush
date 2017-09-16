package com.gopush.common;


import io.netty.util.AttributeKey;

/**
 * go-push
 *
 * @类功能说明：全局常量定义
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/18 下午3:02
 * @VERSION：
 */
public class Constants {


    /*****
     *
     * Device 和 NodeServer 之间
     *
     * ***/
    //绑定设备
    public static final AttributeKey<String> CHANNEL_ATTR_DEVICE = AttributeKey.newInstance("device");

    //绑定的握手状态
    public static final AttributeKey<Boolean> CHANNEL_ATTR_HANDSHAKE = AttributeKey.newInstance("handshake");

    //绑定的心跳间隔 // read ,write, all
    public static final AttributeKey<Integer[]> CHANNEL_ATTR_IDLE = AttributeKey.newInstance("idle");

    /*****
     *
     * NodeServer 与 DataCenter 之间
     *
     * ***/
    public static final AttributeKey<String> CHANNEL_ATTR_DATACENTER = AttributeKey.newInstance("datacenter");


}
