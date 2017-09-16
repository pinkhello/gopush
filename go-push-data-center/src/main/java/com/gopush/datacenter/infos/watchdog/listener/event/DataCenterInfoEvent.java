package com.gopush.datacenter.infos.watchdog.listener.event;

import com.gopush.infos.datacenter.bo.DataCenterInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 上午8:42
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataCenterInfoEvent {
    private String name;
    private DataCenterInfo dataCenterInfo;
}
