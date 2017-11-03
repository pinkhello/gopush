package com.gopush.common.utils.zk.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/11 下午8:58
 */
public interface ZkStateListener {

    default void connectedEvent(CuratorFramework curator, ConnectionState state) {
    }

    default void reconnectedEvent(CuratorFramework curator, ConnectionState state) {
    }

    default void lostEvent(CuratorFramework curator, ConnectionState state) {
    }

}
