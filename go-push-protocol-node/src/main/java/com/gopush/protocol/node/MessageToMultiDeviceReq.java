package com.gopush.protocol.node;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * go-push
 *
 * @类功能说明：单条消息发送给多个设备(连接在单个node上的)
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageToMultiDeviceReq extends NodeMessageReq<MessageToMultiDeviceReq> {

    @JSONField(name = "DEVS")
    private List<String> devices;

    @JSONField(name = "MES")
    private String message;


    @Override
    protected Type type() {
        return Type.OTM;
    }

    @Override
    protected MessageToMultiDeviceReq getThis() {
        return this;
    }


}
