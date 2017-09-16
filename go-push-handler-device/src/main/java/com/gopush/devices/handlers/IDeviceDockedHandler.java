package com.gopush.devices.handlers;

/**
 * go-push
 *
 * @类功能说明：设备上线报告
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/19 上午12:51
 * @VERSION：
 */
public interface IDeviceDockedHandler {

    void upReport(String device, int channelHashCode, int[] idles);
}
