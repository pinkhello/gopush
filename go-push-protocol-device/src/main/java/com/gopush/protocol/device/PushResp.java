package com.gopush.protocol.device;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * go-push
 *
 * @类功能说明：推送消息响应
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushResp extends DeviceMessageResp<PushResp> {

//    private static final String DEVICE_KEY = "D";
//    private final static String MSG_KEY = "ID";
//    private final static String RESULT_KEY = "R";


    public enum Result {
        S,  //SUCCESS,
        D,  //DUPLICATE,
        NR, //NOT_REGISTERED,
        IN  //INTERNAL_ERROR
    }

    @JSONField(name = "D")
    private String device;

    @JSONField(name = "ID")
    private String msgId;

    @JSONField(name = "R")
    private Result result;

    @Override
    protected Type type() {
        return Type.PR;
    }

    @Override
    protected PushResp getThis() throws Exception {
        return this;
    }


}
