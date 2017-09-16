package com.gopush.nodeserver.nodes.stores;

import io.netty.channel.Channel;

import java.util.List;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午1:10
 * @VERSION：
 */
public interface IDataCenterChannelStore {


    List<Channel> getAllChannels();


    boolean contains(String dcId);

    /**
     * 根据 DCID 获取Channel
     *
     * @param dcId
     * @return
     */
    Channel getChannel(String dcId);


    /**
     * 根据channel获取dcid
     *
     * @param channel
     * @return
     */
    String getDcId(Channel channel);

    /**
     * 清空channel
     */
    void clear();


    /**
     * DC-channel 计数
     *
     * @return
     */
    int count();


    /**
     * 检测此channel是不是已经存在的channel，不是的话加入缓存中
     *
     * @param channel
     */
    void isDcChannelToSave(Channel channel);


    /**
     * 检测此channel是不是已经存在的channel，是的话删除缓存中
     *
     * @param channel
     */
    void isDcChannelToRemove(Channel channel);

}
