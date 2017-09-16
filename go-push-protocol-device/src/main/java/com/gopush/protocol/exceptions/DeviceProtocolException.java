package com.gopush.protocol.exceptions;

/**
 * go-push
 *
 * @类功能说明：设备协议异常
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
public class DeviceProtocolException extends RuntimeException {
    public DeviceProtocolException() {
    }

    public DeviceProtocolException(String message) {
        super(message);
    }

    public DeviceProtocolException(Throwable cause) {
        super(cause);
    }

    public DeviceProtocolException(String message, Throwable cause) {
        super(message, cause);
    }


    public DeviceProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
