package com.gopush.protocol.device;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * go-push
 *
 * @类功能说明：握手请求
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandShakeReq extends DeviceMessageReq<HandShakeReq> {

    @JSONField(name = "D")
    private String device;

    @JSONField(name = "TK")
    private String token;

    @JSONField(name = "R_IDLE")
    private int readInterval;
    @JSONField(name = "W_IDLE")
    private int writeInterval;
    @JSONField(name = "A_IDLE")
    private int allInterval;


    @Override
    protected Type type() {
        return Type.HS;
    }

    @Override
    protected HandShakeReq getThis() throws Exception {
        return this;
    }


}
