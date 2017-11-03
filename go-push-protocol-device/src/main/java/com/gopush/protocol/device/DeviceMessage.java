package com.gopush.protocol.device;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.gopush.protocol.exceptions.DeviceProtocolException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * go-push
 *
 * @类功能说明：设备消息基类
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */

public abstract class DeviceMessage<T> {

    /**
     * 消息类型
     * 1)心跳请求
     * 2)心跳响应
     * 3)握手请求
     * 4)握手响应
     * 5)消息推送
     * 6)推送反馈
     */
    protected enum Type {
        PI,    // PING
        PO,   // PONG
        HS,    // HANDSHAKE_REQ
        HSR,  // HANDSHAKE_RESP
        P,    // PUSH_REQ
        PR    // PUSH_RESP
    }

    /**
     * 获取设备消息类型
     *
     * @return
     */
    protected abstract Type type();


    protected abstract T getThis() throws Exception;

    /**
     * 节点消息转换
     *
     * @return
     * @throws Exception
     */
    protected String toEncode() throws Exception {
        return JSON.toJSONString(getThis());
    }


    /**
     * 设备消息编码
     *
     * @return
     */
    public String encode() throws DeviceProtocolException {
        try {

            Message message = Message
                    .builder()
                    .type(type())
                    .message(toEncode())
                    .build();
            return JSON.toJSONString(message);
        } catch (Exception e) {
            throw new DeviceProtocolException(e);
        }
    }


    /**
     * 设备消息解码
     *
     * @param json
     * @return
     * @throws DeviceProtocolException
     */
    public static DeviceMessage decode(String json) throws DeviceProtocolException {
        try {

            Message msg = JSON.parseObject(json, Message.class);

            Class cls = null;

            switch (msg.type) {
                case PI:
                    cls = Ping.class;
                    break;
                case PO:
                    cls = Pong.class;
                    break;
                case HS:
                    cls = HandShakeReq.class;
                    break;
                case HSR:
                    cls = HandShakeResp.class;
                    break;
                case P:
                    cls = PushReq.class;
                    break;
                case PR:
                    cls = PushResp.class;
                    break;
                default:
                    throw new DeviceProtocolException("Unknown Device type " + msg.type);
            }
            DeviceMessage message = (DeviceMessage) JSON.parseObject(msg.message, cls);
            return message;
        } catch (Exception e) {
            throw new DeviceProtocolException("Exception occur,Message is " + json, e);
        }
    }


    //真正的传递消息的类

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Message {
        @JSONField(name = "T")
        private Type type;

        @JSONField(name = "M")
        private String message;

    }


}
