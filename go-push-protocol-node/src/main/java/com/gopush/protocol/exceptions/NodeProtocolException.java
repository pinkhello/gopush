package com.gopush.protocol.exceptions;

/**
 * go-push
 *
 * @类功能说明：服务节点协议异常
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
public class NodeProtocolException extends RuntimeException {

    public NodeProtocolException() {
    }

    public NodeProtocolException(String message) {
        super(message);
    }

    public NodeProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public NodeProtocolException(Throwable cause) {
        super(cause);
    }

    public NodeProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
