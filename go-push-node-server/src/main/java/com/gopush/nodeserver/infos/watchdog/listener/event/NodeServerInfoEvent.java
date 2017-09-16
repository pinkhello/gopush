package com.gopush.nodeserver.infos.watchdog.listener.event;

import com.gopush.infos.nodeserver.bo.NodeServerInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/12 上午12:40
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeServerInfoEvent {
    private String name;
    private NodeServerInfo nodeServerInfo;
}
