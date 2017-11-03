package com.gopush.nodeserver.devices;

import com.gopush.nodeserver.config.GoPushNodeServerConfig;
import com.gopush.nodeserver.devices.inbound.DeviceChannelInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * go-push
 *
 * @类功能说明：设备服务启动器
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/18 下午10:59
 * @VERSION：
 */

@Slf4j
@Component
public class DeviceServerBootstrap {

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    @Autowired
    private GoPushNodeServerConfig goPushNodeServerConfig;

    @Autowired
    private DeviceChannelInboundHandler deviceChannelInboundHandler;

    @PostConstruct
    public void start() throws Exception {


        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channelFactory(NioServerSocketChannel::new)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {

                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("logHandler", new LoggingHandler());
                        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                        pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast("idleStateHandler", new IdleStateHandler(300, 0, 0));

                        pipeline.addLast("handler", deviceChannelInboundHandler);
                    }
                })

                .option(ChannelOption.SO_BACKLOG, 1000000)  //连接队列深度
                .option(ChannelOption.TCP_NODELAY, true)   //设置 no_delay
                .option(ChannelOption.SO_SNDBUF, 2048).option(ChannelOption.SO_RCVBUF, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_SNDBUF, 2048).childOption(ChannelOption.SO_RCVBUF, 1024)
                .childOption(ChannelOption.SO_LINGER, 0);

        bootstrap.bind(goPushNodeServerConfig.getDevicePort()).sync();
        log.info("device server start successful! listening port: {}", goPushNodeServerConfig.getDevicePort());
    }


    @PreDestroy
    public void destory() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

}
