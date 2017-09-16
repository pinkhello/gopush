package com.gopush.nodeserver.devices.handlers;

import com.gopush.common.Constants;
import com.gopush.common.constants.RedisKeyEnum;
import com.gopush.common.utils.ip.IpUtils;
import com.gopush.devices.handlers.IDeviceDisconnectHandler;
import com.gopush.nodeserver.devices.BatchProcessor;
import com.gopush.nodeserver.devices.stores.IDeviceChannelStore;
import com.gopush.nodeserver.nodes.senders.INodeSender;
import com.gopush.protocol.node.DeviceDisconReq;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * go-push
 *
 * @类功能说明： 批处理设备断连接后需要触发上报
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午12:25
 * @VERSION：
 */

@Slf4j
@Component
public class DeviceDeviceDisconnectHandler extends BatchProcessor<Object[]> implements IDeviceDisconnectHandler {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IDeviceChannelStore deviceChannelStore;

    @Autowired
    private INodeSender nodeSender;


    @Override
    public void channelClosed(Channel channel) {
        String device = (String) channel.attr(Constants.CHANNEL_ATTR_DEVICE).get();
        if (StringUtils.isNotEmpty(device)) {
            //移除设备-channel 映射
            deviceChannelStore.removeChannel(device, channel);
            putMsg(new Object[]{device, channel.hashCode()});
        }
        log.info("channel closed , channel:{}, device:{}", channel, device);
    }

    @Override
    protected String getBatchExecutorName() {
        return "DeviceDisconnect-BatchExecutor";
    }

    @Override
    protected boolean retryFailure() {
        return false;
    }

    @Override
    protected void batchHandler(List<Object[]> batchReq) throws Exception {

        // 因为异步,要求 channel id 一样才能移除,防止 异步时间差删除了新建的Channel
        if (CollectionUtils.isNotEmpty(batchReq)) {
            String nodeIp = IpUtils.intranetIp();
            DeviceDisconReq req = DeviceDisconReq.builder().node(nodeIp).build();
            final boolean[] flag = {Boolean.FALSE};
            batchReq.stream().forEach((ele) -> {
                String device = (String) ele[0];
                int channelHashCode = (int) ele[1];

                String channel = (String) redisTemplate.opsForHash().get(
                        RedisKeyEnum.DEVICE_KEY.getValue() + device,
                        RedisKeyEnum.DEVICE_CHANNEL_FIELD.getValue());
                if (channel != null && Integer.parseInt(channel) == channelHashCode) {
                    if (!flag[0]) {
                        flag[0] = Boolean.TRUE;
                    }
                    req.addDevice(device);
                    redisTemplate.opsForHash().delete(RedisKeyEnum.DEVICE_KEY.getValue() + device, RedisKeyEnum.DEVICE_CHANNEL_FIELD.getValue());
                    redisTemplate.opsForHash().delete(RedisKeyEnum.DEVICE_KEY.getValue() + device, RedisKeyEnum.DEVICE_NODE_FIELD.getValue());
                }
            });

            if (flag[0]) {
                nodeSender.sendShuffle(req);
            }

        }

    }
}
