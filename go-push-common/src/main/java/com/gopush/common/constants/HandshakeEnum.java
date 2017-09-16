package com.gopush.common.constants;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 上午12:05
 */
@Getter
public enum HandshakeEnum {
    HANDSAHKE_OK(200, "握手成功"),
    HANDSHAKE_INVALID_DEVICE(300, "非法设备"),
    HANDSHAKE_INVALID_TOKEN(301, "非法Token");
    private int key;
    private String descri;

    HandshakeEnum(int key, String descri) {
        this.key = key;
        this.descri = descri;
    }

    public static HandshakeEnum fromKey(int key) {
        if (key <= 0) {
            return null;
        }
        return Arrays.stream(HandshakeEnum.values())
                .filter(a -> key == a.getKey())
                .findFirst()
                .orElse(null);
    }
}
