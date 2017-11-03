package com.gopush.datacenter.nodes.manager;

import com.gopush.datacenter.nodes.inbound.NodeChannelInBoundHandler;
import com.gopush.nodes.handlers.INodeMessageHandler;
import com.gopush.protocol.node.NodeMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/7/1 上午8:35
 * @VERSION：
 */

@Slf4j
@EqualsAndHashCode
@Data
public class Node implements INode {

    private static final int INT_ZERO = 0;
    private static final int INT_MAX_VAL = Integer.MAX_VALUE - 1;


    private String name;
    /**
     * 内网IP
     */
    private String intranetIp;

    /**
     * 内网端口 node-port
     */
    private int nodePort;

    /**
     * 对外IP DEVICE
     */
    private String internetIp;

    /**
     * 对外端口 device-port
     */
    private int devicePort;

    /**
     * loopgroup  所有的客户端共用一个
     */
    private transient EventLoopGroup group;

    /**
     * 处理器设置
     */
    private transient volatile List<INodeMessageHandler> nodeMessageHandlers;


    /**
     * 是否存活
     */
    private volatile boolean alive = Boolean.FALSE;

    /**
     * 是否初始化过
     */
    private volatile boolean initialized = Boolean.FALSE;

    /**
     * 是否销毁过
     */
    private volatile boolean destroyed = Boolean.FALSE;

    /**
     * 失败的请求
     */
    private transient Queue<NodeMessage> failMessage = new ConcurrentLinkedQueue<>();

    private volatile AtomicInteger receiveCounter = new AtomicInteger(0);

    private volatile AtomicInteger sendCounter = new AtomicInteger(0);


    /**
     * 各自客户端的channel
     */
    private transient Channel channel;

    public Node(String name,
                String intranetIp, int nodePort,
                String internetIp, int devicePort,
                EventLoopGroup group, List<INodeMessageHandler> nodeMessageHandlers) {
        this.name = name;
        this.intranetIp = intranetIp;
        this.nodePort = nodePort;
        this.internetIp = internetIp;
        this.devicePort = devicePort;
        this.group = group;
        this.nodeMessageHandlers = nodeMessageHandlers;
    }

    @Override
    public void init() {
//        log.info("node  init ........");
        destroyed = Boolean.FALSE;
        try {
            connect();
        } catch (Exception e) {
            log.error("init data center connect node-server  error:{}", e);
        } finally {
            initialized = Boolean.TRUE;
        }

    }

    @Override
    public void destroy() {
//        log.info("node -------> destroy");
        if (failMessage != null) {
            if (!failMessage.isEmpty()) {
                log.info("destroy node lost retry messages: {}", failMessage.toString());
                failMessage.clear();
            }
        }
        failMessage = null;
        nodeMessageHandlers = null;
        group = null;
        if (channel != null) {
            if (channel.isActive() || channel.isOpen() || channel.isRegistered()) {
                channel.close();
            }
        }
        receiveCounter.set(INT_ZERO);
        sendCounter.set(INT_ZERO);
        alive = Boolean.FALSE;
        initialized = Boolean.FALSE;
        destroyed = Boolean.TRUE;
    }


    @Override
    public void active() {
        alive = Boolean.TRUE;
    }

    @Override
    public void inactive() {
        alive = Boolean.FALSE;
    }


    @Override
    public void send(NodeMessage message) {
        send(message, true);
    }

    @Override
    public void send(NodeMessage message, boolean retry) {
        if (!initialized) {
            log.warn("node client not init, nodeInfo: {}", toString());
            if (retry) {
                failMessage.add(message);
            }
            return;
        }
        if (channel == null) {
            log.warn("channel of node is empty! nodeInfo: {}", toString());
            if (retry) {
                failMessage.add(message);
            }
            return;
        }
        channel.writeAndFlush(message.encode()).addListener(channelFuture -> {
            if (!channelFuture.isSuccess()) {
                if (retry) {
                    failMessage.add(message);
                }
                return;
            }
            int count = sendCounter.incrementAndGet();
            if (count >= INT_MAX_VAL) {
                sendCounter.set(INT_ZERO);
            }
        });

    }

    @Override
    public void retrySendFail() {
        if (!failMessage.isEmpty()) {
            while (true) {
                try {
                    send(failMessage.remove());
                } catch (NoSuchElementException ex) {
                    break;
                }
            }
        }
    }

    @Override
    public void reconnect(ChannelHandlerContext ctx) {
        if (destroyed) {
            return;
        }
        inactive();
        EventLoop loop = ctx.channel().eventLoop();
        loop.schedule(() -> connect(), 2, TimeUnit.SECONDS);
    }

    @Override
    public void handle(ChannelHandlerContext ctx, NodeMessage message) {
        int count = receiveCounter.incrementAndGet();
        if (count >= INT_MAX_VAL) {
            receiveCounter.set(INT_ZERO);
        }
        if (!nodeMessageHandlers.isEmpty()) {
            nodeMessageHandlers.stream().forEach(handler -> {
                try {
                    if (handler.support(message)) {
                        handler.call(ctx, message);
                    }
                } catch (Exception e) {
                    log.error("Node handle call Exception, error:{}", e);
                }
            });
        }
    }

    @Override
    public int receiveCounter() {
        return receiveCounter.get();
    }

    @Override
    public int sendCounter() {
        return sendCounter.get();
    }


    private void connect() {
        channel = configBootstrap().connect().channel();
    }

    private Bootstrap configBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .remoteAddress(intranetIp, nodePort)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(channelInitializer());
        return bootstrap;
    }

    private ChannelInitializer channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast("idleStateHandler", new IdleStateHandler(300, 0, 0));
                pipeline.addLast("handler", nodeChannelInBoundHandler());
            }
        };
    }

    private NodeChannelInBoundHandler nodeChannelInBoundHandler() {
        return new NodeChannelInBoundHandler(this);
    }

}
