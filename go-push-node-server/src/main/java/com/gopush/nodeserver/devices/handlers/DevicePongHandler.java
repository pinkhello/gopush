package com.gopush.nodeserver.devices.handlers;

import com.gopush.common.Constants;
import com.gopush.devices.handlers.IDeviceMessageHandler;
import com.gopush.protocol.device.DeviceMessage;
import com.gopush.protocol.device.Pong;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * go-push
 *
 * @类功能说明：PONG请求批处理器
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/12 下午10:03
 * @VERSION：
 */

@Slf4j
@Component
public class DevicePongHandler extends PingPongProcessor<Object[]> implements IDeviceMessageHandler<Pong> {

    @Override
    public boolean support(DeviceMessage message) {
        return message instanceof Pong;
    }

    @Override
    public void call(ChannelHandlerContext context, Pong message) {

        Channel channel = context.channel();
        if (!checkHandShake(channel)) {
            context.close();
            return;
        }
        putMsg(new Object[]{
                channel.attr(Constants.CHANNEL_ATTR_DEVICE).get(),
                channel.attr(Constants.CHANNEL_ATTR_IDLE).get()});
        log.debug("receive pong, channel:{}, device:{}", channel, channel.attr(Constants.CHANNEL_ATTR_DEVICE).get());

    }

    @Override
    protected String getBatchExecutorName() {
        return "Pong-BatchExecutor";
    }

    @Override
    protected boolean retryFailure() {
        return false;
    }

    @Override
    protected void batchHandler(List<Object[]> batchReq) throws Exception {

        //发出去的PING请求的响应
        //也可以设置 保活设置
        liveHandShake(batchReq);

    }

}
