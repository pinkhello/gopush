package com.gopush.nodeserver.devices.handlers;

import com.gopush.common.Constants;
import com.gopush.common.constants.RedisKeyEnum;
import com.gopush.nodeserver.devices.BatchProcessor;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/22 上午1:28
 * @VERSION：
 */
@Slf4j
@Component
public abstract class PingPongProcessor<T> extends BatchProcessor<T> {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 检测是否已经握手
     *
     * @param channel
     * @return
     */
    protected boolean checkHandShake(Channel channel) {
        if (!channel.hasAttr(Constants.CHANNEL_ATTR_HANDSHAKE)) {
            log.warn("channel not handshake, channel:{}", channel);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


    protected void liveHandShake(List<Object[]> batchReq) {
        if (CollectionUtils.isNotEmpty(batchReq)) {

            batchReq.stream().forEach((ele) -> {
                String device = (String) ele[0];
                int[] idles = (int[]) ele[1];
                redisTemplate.expire(RedisKeyEnum.DEVICE_KEY.getValue() + device, idles[0], TimeUnit.SECONDS);
            });

        }
    }

}
