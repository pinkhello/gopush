package com.gopush.nodeserver.devices.inbound;

import com.gopush.devices.handlers.IDeviceMessageHandler;
import com.gopush.nodeserver.devices.handlers.DeviceDeviceDisconnectHandler;
import com.gopush.protocol.device.DeviceMessage;
import com.gopush.protocol.device.Ping;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/20 上午12:46
 * @VERSION：
 */

@Slf4j
@ChannelHandler.Sharable
@Component
public class DeviceChannelInboundHandler extends SimpleChannelInboundHandler<String> {


    private static final String PING = Ping.builder().build().encode();

    @Autowired
    private DeviceDeviceDisconnectHandler deviceDisconnectHandler;

    @Autowired
    private List<IDeviceMessageHandler> deviceMessageHandlers;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {

        log.debug("channel:{}, message:{}", ctx.channel(), message);
        DeviceMessage deviceMessage = DeviceMessage.decode(message);

        if (!deviceMessageHandlers.isEmpty()) {
            deviceMessageHandlers.stream().forEach((handler) -> {
                try {
                    if (handler.support(deviceMessage)) {
                        handler.call(ctx, deviceMessage);
                    }
                } catch (Exception e) {
                    log.error("exception error:{}", e);
                }
            });
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active, channel:{}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel inactive, channel:{}", ctx.channel());
        deviceDisconnectHandler.channelClosed(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("exception error:{}, channel:{}", cause.getMessage(), ctx.channel());
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(PING);
            }
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(PING);
            }
            if (event.state() == IdleState.ALL_IDLE) {
                ctx.writeAndFlush(PING);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
