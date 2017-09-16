package com.gopush.nodeserver.nodes.senders;


import com.gopush.protocol.node.NodeMessage;

/**
 * go-push
 *
 * @类功能说明：数据中心发生类
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午1:01
 * @VERSION：
 */
public interface INodeSender<T extends NodeMessage> {


    /**
     * 向指定的数据中心发送
     *
     * @param dcId
     * @param message
     */
    void send(String dcId, T message);


    /**
     * 随机选择一台发送
     *
     * @param message
     */
    void sendShuffle(T message);

    /**
     * 全部数据中心发送
     *
     * @param message
     */
    void send(T message);

}
