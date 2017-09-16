package com.gopush.common.constants;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/12 上午3:16
 */
@Getter
public enum ZkGroupEnum {
    NODE_SERVER("/NODE-SERVER"),
    DATA_CENTER("/DATA-CENTER");
    private String value;

    ZkGroupEnum(String value) {
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
