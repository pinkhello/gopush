package com.gopush.infos.datacenter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 上午7:56
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataCenterInfo {
    private String name;
    //内网IP
    private String intranetIp;
    //外网IP
    private String internetIp;

    private List<RestfulLoaderInfo> restfulLoaderInfos;

    private List<NodeClientLoaderInfo> nodeClientLoaderInfos;

}
