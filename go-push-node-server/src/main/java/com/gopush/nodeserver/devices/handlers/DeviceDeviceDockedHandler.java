package com.gopush.nodeserver.devices.handlers;

import com.gopush.common.constants.RedisKeyEnum;
import com.gopush.common.utils.ip.IpUtils;
import com.gopush.devices.handlers.IDeviceDockedHandler;
import com.gopush.nodeserver.devices.BatchProcessor;
import com.gopush.nodeserver.nodes.senders.INodeSender;
import com.gopush.protocol.node.DeviceDockedReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午12:58
 * @VERSION：
 */

@Slf4j
@Component
public class DeviceDeviceDockedHandler extends BatchProcessor<Object[]> implements IDeviceDockedHandler {


    @Autowired
    private INodeSender nodeSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void upReport(String device, int channelHashCode, int[] idles) {
        putMsg(new Object[]{device, channelHashCode, idles});
        log.info("up report device docked, device:{}, channelHashCode:{}, idles:{}", device, channelHashCode, Arrays.toString(idles));
    }

    @Override
    protected String getBatchExecutorName() {
        return "DeviceDocked-BatchExecutor";
    }

    @Override
    protected boolean retryFailure() {
        return true;
    }

    @Override
    protected void batchHandler(List<Object[]> batchReq) throws Exception {
        //添加缓存 设备-节点-channel 绑定
        if (CollectionUtils.isNotEmpty(batchReq)) {
            String nodeIp = IpUtils.intranetIp();
            DeviceDockedReq req = DeviceDockedReq.builder().node(nodeIp).build();
            batchReq.stream().forEach((ele) -> {
                req.addDevice((String) ele[0]);
                Map<String, String> hash = new HashMap<>();
                hash.put(RedisKeyEnum.DEVICE_CHANNEL_FIELD.getValue(), String.valueOf(ele[1]));
                hash.put(RedisKeyEnum.DEVICE_NODE_FIELD.getValue(), nodeIp);
                int[] idles = (int[]) ele[2];
                redisTemplate.opsForHash().put(RedisKeyEnum.DEVICE_KEY.getValue() + ele[0], hash, idles[0]);
            });
            //将需要上报的device 加到list 构造上报请求 使用 nodeSender 发送出去
            nodeSender.sendShuffle(req);
        }

    }
}
