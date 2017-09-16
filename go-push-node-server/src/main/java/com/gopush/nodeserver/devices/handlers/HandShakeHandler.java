package com.gopush.nodeserver.devices.handlers;

import com.gopush.common.Constants;
import com.gopush.common.constants.HandshakeEnum;
import com.gopush.common.constants.IdleEnum;
import com.gopush.common.constants.RedisKeyEnum;
import com.gopush.devices.handlers.IDeviceDockedHandler;
import com.gopush.devices.handlers.IDeviceMessageHandler;
import com.gopush.nodeserver.devices.BatchProcessor;
import com.gopush.nodeserver.devices.stores.IDeviceChannelStore;
import com.gopush.protocol.device.DeviceMessage;
import com.gopush.protocol.device.HandShakeReq;
import com.gopush.protocol.device.HandShakeResp;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * go-push
 *
 * @类功能说明：握手请求批处理器
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/12 下午10:03
 * @VERSION：
 */

@Slf4j
@Component
public class HandShakeHandler extends BatchProcessor<Object[]> implements IDeviceMessageHandler<HandShakeReq> {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IDeviceDockedHandler deviceDockedHandler;

    @Autowired
    private IDeviceChannelStore deviceChannelStore;

    @Override
    public boolean support(DeviceMessage message) {
        return message instanceof HandShakeReq;
    }

    @Override
    public void call(ChannelHandlerContext context, HandShakeReq message) {
        putMsg(new Object[]{context.channel(), message});
        log.info("handshake request received, message:{}, channel:{}", message, context.channel());
    }


    @Override
    protected String getBatchExecutorName() {
        return "HandShake-BatchExecutor";
    }

    @Override
    protected boolean retryFailure() {
        return false;
    }


    //握手成功
//    public static final int HANDSAHKE_OK = 200;

    //非法设备
//    public static final int HANDSHAKE_INVALID_DEVICE = 300;

    //非法token
//    public static final int HANDSHAKE_INVALID_TOKEN = 301;

    @Override
    protected void batchHandler(List<Object[]> batchReq) throws Exception {
        if (CollectionUtils.isNotEmpty(batchReq)) {
            //先全部取出redis 中存储的要处理的设备的列表
            batchReq.stream().forEach((e) -> {

                try {
                    Channel channel = (Channel) e[0];
                    HandShakeReq req = (HandShakeReq) e[1];

                    String devcieId = req.getDevice();
                    //建立 握手响应
                    HandShakeResp.HandShakeRespBuilder respBuilder =
                            HandShakeResp.builder();
                    if (StringUtils.isEmpty(req.getDevice())) {
                        respBuilder.result(HandshakeEnum.HANDSHAKE_INVALID_DEVICE.getKey());
                    } else {
                        String token = (String) redisTemplate.opsForHash().get(
                                RedisKeyEnum.DEVICE_KEY.getValue() + devcieId,
                                RedisKeyEnum.DEIVCE_TOKEN_FIELD.getValue());
                        //所有的token 都不为空 且 两个token相等
                        if (StringUtils.isAnyEmpty(token, req.getToken()) || !StringUtils.equals(req.getToken(), token)) {
                            respBuilder.result(HandshakeEnum.HANDSHAKE_INVALID_TOKEN.getKey());
                        } else {
                            respBuilder.result(HandshakeEnum.HANDSAHKE_OK.getKey());
                        }
                    }
                    HandShakeResp resp = respBuilder.build();

                    String respEncode = resp.encode();
                    //握手不成功

                    if (resp.getResult() != HandshakeEnum.HANDSAHKE_OK.getKey()) {
                        //将写出握手响应后关闭链接
                        channel.writeAndFlush(respEncode).addListener(ChannelFutureListener.CLOSE);
                        log.info("handshake fail, channel:{}, device:{}, response:{}", channel, req.getDevice(), respEncode);
                    } else {


                        //将已经存在的链接关闭
                        Channel exist = deviceChannelStore.getChannel(devcieId);
                        if (exist != null) {
                            log.warn("exist channel - device in cache, channel:{},device:{}", exist, devcieId);
                            exist.close();
                        }


                        //将握手结果, 设备信息  绑定到 通道属性里面
                        Integer[] idles = new Integer[]{
                                req.getReadInterval() + IdleEnum.READ_IDLE.getValue(),
                                req.getWriteInterval() + IdleEnum.WRITE_IDLE.getValue(),
                                req.getAllInterval() + IdleEnum.ALL_IDLE.getValue()
                        };
                        channel.attr(Constants.CHANNEL_ATTR_IDLE).set(idles);
                        channel.attr(Constants.CHANNEL_ATTR_DEVICE).set(devcieId);
                        channel.attr(Constants.CHANNEL_ATTR_HANDSHAKE).set(Boolean.TRUE);

                        //重设读写超时器
                        channel.pipeline().replace("idleStateHandler", "idleStateHandler", new IdleStateHandler(idles[0], idles[1], idles[2], TimeUnit.SECONDS));

                        //添加本地 设备-channel 绑定
                        deviceChannelStore.addChannel(devcieId, channel);

                        //报告设备上线
                        deviceDockedHandler.upReport(devcieId, channel.hashCode(), new int[]{idles[0], idles[1], idles[2]});

                        //写出握手响应
                        channel.writeAndFlush(respEncode);
                        log.info("handshake successful, channel:{}, device:{}, message:{}", channel, req.getDevice(), respEncode);


                    }
                } catch (Exception ex) {
                    log.error("handshake error:{}", ex);
                }

            });

        }

    }


}
