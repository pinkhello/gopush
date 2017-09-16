package com.gopush.infos.nodeserver.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/12 上午12:17
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeServerInfo {
    //名称
    private String name;
    //内网IP
    private String intranetIp;
    //外网IP
    private String internetIp;
    //对设备监听端口
    private int devicePort;
    //对内监听端口
    private int nodePort;
    //系统负载信息
    private NodeLoaderInfo nodeLoaderInfo;
}
