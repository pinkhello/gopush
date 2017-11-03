package com.gopush.protocol.node;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class MessageToMultiDeviceResp extends NodeMessageResp<MessageToMultiDeviceResp> {

    @JSONField(name = "R")
    private int result;

    @Override
    protected Type type() {
        return Type.OTMS;
    }

    @Override
    protected MessageToMultiDeviceResp getThis() {
        return this;
    }


}
