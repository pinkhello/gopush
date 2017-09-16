package com.gopush.common.constants;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 下午5:31
 */
@Getter
public enum RestfulRespEnum {

    OK(0, "成功");

    private int key;
    private String descri;

    RestfulRespEnum(int key, String descri) {
        this.key = key;
        this.descri = descri;
    }


    public static RestfulRespEnum fromKey(int key) {
        if (key <= 0) {
            return null;
        }
        return Arrays.stream(RestfulRespEnum.values())
                .filter(a -> key == a.getKey())
                .findFirst()
                .orElse(null);
    }
}
