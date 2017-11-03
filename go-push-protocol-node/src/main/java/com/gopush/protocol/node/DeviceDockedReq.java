package com.gopush.protocol.node;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * go-push
 *
 * @类功能说明：设备上线上报请求
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDockedReq extends NodeMessageReq<DeviceDockedReq> {

    //需要上报的设备列表(是批量上报的)
    @JSONField(name = "DEVS")
    private List<String> devices;


    @JSONField(name = "N")
    private String node;

    /**
     * 添加设备
     *
     * @param device
     */

    public void addDevice(String device) {
        if (devices == null) {
            devices = new ArrayList<>();
        }
        if (!devices.contains(device)) {
            devices.add(device);
        }
    }


    @Override
    protected Type type() {
        return Type.DO;
    }

    @Override
    protected DeviceDockedReq getThis() {
        return this;
    }


}
