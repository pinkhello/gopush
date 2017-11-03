package com.gopush.protocol.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * go-push
 *
 * @类功能说明：Ping-Pong 心跳
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
@Builder
@Data
@AllArgsConstructor
public class Ping extends DeviceMessageReq<Ping> {

    @Override
    protected Type type() {
        return Type.PI;
    }

    @Override
    protected Ping getThis() throws Exception {
        return this;
    }


}
