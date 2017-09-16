package com.gopush.nodeserver.nodes.handlers;

import com.gopush.nodeserver.nodes.stores.IDataCenterChannelStore;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/22 上午1:34
 * @VERSION：
 */
public abstract class NodeBaseHandler {


    @Autowired
    private IDataCenterChannelStore dataCenterChannelStore;

    protected void saveLiveDc(Channel channel) {
        dataCenterChannelStore.isDcChannelToSave(channel);
    }

}
