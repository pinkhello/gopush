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
 * @类功能说明：多条消息发送给单个设备(连接在单个node上的)
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiMessageToDeviceReq extends NodeMessageReq<MultiMessageToDeviceReq> {


    @JSONField(name = "MESS")
    private List<String> messages;

    @JSONField(name = "DEV")
    private String device;

    @Override
    protected Type type() {
        return Type.MTO;
    }

    @Override
    protected MultiMessageToDeviceReq getThis() {
        return this;
    }


}
