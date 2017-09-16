package com.gopush.common.constants;

import lombok.Getter;

import java.util.Arrays;

/**
 * go-push
 *
 * @author：喝咖啡的囊地鼠
 * @date：2017/7/11 上午12:14
 */
@Getter
public enum RedisKeyEnum {

    DEVICE_KEY("DE:"),
    DEIVCE_TOKEN_FIELD("token"),
    DEVICE_NODE_FIELD("node"),
    DEVICE_CHANNEL_FIELD("channel");

    private String value;

    RedisKeyEnum(String value) {
        this.value = value;
    }

    public static IdleEnum fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Arrays.stream(IdleEnum.values())
                .filter(a -> value.equals(a.getValue()))
                .findFirst()
                .orElse(null);
    }

}
