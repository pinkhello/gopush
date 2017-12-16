package com.gopush.protocol.node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.gopush.protocol.exceptions.NodeProtocolException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * go-push
 *
 * @类功能说明：节点服务消息基类
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
public abstract class NodeMessage<T> {

    //消息 Type Key

    /**
     * 消息类型
     * 1)心跳请求
     * 2)心跳响应
     * 3)设备上线请求
     * 4)设备上线响应
     * 5)设备断线请求
     * 6)设备断线响应
     * 7)多条消息发送给一个设备请求
     * 8)多条消息发送给一个设备响应
     * 9)单条消息发送给多个设备请求
     * 10)单条消息发送给多个设备响应
     * 11)节点服务信息请求
     * 12)节点服务信息响应
     */
    protected enum Type {
        PI,       //PING
        PO,       //PONG
        DO,       //DEVICE_DOCKED_REQ
        DOS,      //DEVICE_DOCKED_RESP
        DI,       //DEVICE_DISCON_REQ
        DIS,      //DEVICE_DISCON_RESP
        MTO,      //MULTI_MSG_TO_ONE_DEVICE_REQ
        MTOS,     //MULTI_MSG_TO_ONE_DEVICE_RESP
        OTM,     //ONE_MSG_TO_MULTI_DEVICE_REQ
        OTMS,     //ONE_MSG_TO_MULTI_DEVICE_RESP
        NI,     //NODE_INFO_REQ
        NIS,     //NODE_INFO_RESP
    }

    /**
     * 获取节点消息类型
     *
     * @return
     */
    protected abstract Type type();


    protected abstract T getThis();

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
     * 节点消息编码
     *
     * @return
     */
    public String encode() throws NodeProtocolException {
        try {
            Message message = Message
                    .builder()
                    .type(type())
                    .message(toEncode())
                    .build();
//            System.out.println("Node message json:   "+ JSON.toJSONString(message));
            return JSON.toJSONString(message);
        } catch (Exception e) {
            throw new NodeProtocolException(e);
        }
    }


    /**
     * 节点消息解码
     *
     * @param json
     * @return
     * @throws NodeProtocolException
     */
    public static NodeMessage decode(String json) throws NodeProtocolException {
        try {

            Message msg = JSON.parseObject(json, Message.class);
            NodeMessage message;

            Class cls;
            switch (msg.type) {
                case PI:
                    cls = Ping.class;
                    break;
                case PO:
                    cls = Pong.class;
                    break;
                case DO:
                    cls = DeviceDockedReq.class;
                    break;
                case DOS:
                    cls = DeviceDockedResp.class;
                    break;
                case DI:
                    cls = DeviceDisconReq.class;
                    break;
                case DIS:
                    cls = DeviceDisconResp.class;
                    break;
                case MTO:
                    cls = MultiMessageToDeviceReq.class;
                    break;
                case MTOS:
                    cls = MultiMessageToDeviceResp.class;
                    break;
                case OTM:
                    cls = MessageToMultiDeviceReq.class;
                    break;
                case OTMS:
                    cls = MessageToMultiDeviceResp.class;
                    break;
                case NI:
                    cls = NodeInfoReq.class;
                    break;
                case NIS:
                    cls = NodeInfoResp.class;
                    break;
                default:
                    throw new NodeProtocolException("Unknown Node type " + msg.type);

            }
            message = (NodeMessage) JSON.parseObject(msg.message, cls);
            return message;
        } catch (Exception e) {
            throw new NodeProtocolException("Exception occur,Message is " + json, e);
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
