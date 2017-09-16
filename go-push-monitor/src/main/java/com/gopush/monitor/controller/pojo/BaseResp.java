package com.gopush.monitor.controller.pojo;

import com.gopush.common.constants.RestfulRespEnum;
import lombok.*;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 下午5:27
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class BaseResp<T> {
    private Integer code;
    private String description;
    private T data;

    public static <T> BaseResp<T> ok(T data) {
        return BaseResp.<T>builder()
                .data(data)
                .code(RestfulRespEnum.OK.getKey())
                .description(RestfulRespEnum.OK.getDescri())
                .<T>build();
    }

    public static BaseResp ok() {
        return ok(null);
    }

    public static BaseResp fail(int failCode, String failMsg) {
        return fail(failCode, failMsg, null);
    }

    public static <T> BaseResp<T> fail(int failCode, String failMsg, T data) {
        return BaseResp.<T>builder().code(failCode).description(failMsg).data(data).<T>build();
    }
}
