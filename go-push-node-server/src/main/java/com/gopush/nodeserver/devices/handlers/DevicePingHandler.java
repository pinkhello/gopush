package com.gopush.nodeserver.devices.handlers;

import com.gopush.common.Constants;
import com.gopush.devices.handlers.IDeviceMessageHandler;
import com.gopush.protocol.device.DeviceMessage;
import com.gopush.protocol.device.Ping;
import com.gopush.protocol.device.Pong;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * go-push
 *
 * @类功能说明：PING请求批处理器
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/12 下午10:03
 * @VERSION：
 */

@Slf4j
@Component
public class DevicePingHandler extends PingPongProcessor<Object[]> implements IDeviceMessageHandler<Ping> {


    //响应
    private static final String PONG = Pong.builder().build().encode();


    @Override
    public boolean support(DeviceMessage message) {
        return message instanceof Ping;
    }

    @Override
    public void call(ChannelHandlerContext context, Ping message) {

        Channel channel = context.channel();
        if (!checkHandShake(channel)) {
            context.close();
            return;
        }
        channel.writeAndFlush(PONG);
        putMsg(new Object[]{
                channel.attr(Constants.CHANNEL_ATTR_DEVICE).get(),
                channel.attr(Constants.CHANNEL_ATTR_IDLE).get()});

        log.debug("receive ping, channel:{}, device:{}", channel, channel.attr(Constants.CHANNEL_ATTR_DEVICE).get());
    }


    @Override
    protected String getBatchExecutorName() {
        return "Ping-BatchExecutor";
    }

    @Override
    protected boolean retryFailure() {
        return false;
    }


    /**
     * 设置设备在线的过期时间
     *
     * @param batchReq
     * @throws Exception
     */
    @Override
    protected void batchHandler(List<Object[]> batchReq) throws Exception {
        //收到设备发送过来的 PING 请求
        liveHandShake(batchReq);
    }

}
