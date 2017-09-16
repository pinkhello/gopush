package com.gopush.common.constants;


import lombok.Getter;

import java.util.Arrays;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 上午12:13
 */
@Getter
public enum NodeMessageEnum {

    OK(200, "OK"),
    FAIL(500, "FAIL");


    private int code;
    private String descri;

    NodeMessageEnum(int code, String descri) {
        this.code = code;
        this.descri = descri;
    }

    public static NodeMessageEnum fromCode(int code) {
        if (code <= 0) {
            return null;
        }
        return Arrays.stream(NodeMessageEnum.values())
                .filter(a -> code == a.getCode())
                .findFirst()
                .orElse(null);
    }
}
