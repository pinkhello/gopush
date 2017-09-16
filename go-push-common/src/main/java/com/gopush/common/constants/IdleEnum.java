package com.gopush.common.constants;

import lombok.Getter;

import java.util.Arrays;

/**
 * go-push
 *
 * @author：喝咖啡的囊地鼠
 * @date：2017/7/10 下午11:38
 */
@Getter
public enum IdleEnum {
    READ_IDLE(10),
    WRITE_IDLE(30),
    ALL_IDLE(50);
    private int value;

    IdleEnum(int value) {
        this.value = value;
    }

    public static IdleEnum fromValue(int value) {
        if (value <= 0) {
            return null;
        }
        return Arrays.stream(IdleEnum.values())
                .filter(a -> value == a.getValue())
                .findFirst()
                .orElse(null);
    }
}
