package com.gopush.infos.nodeserver.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/20 下午8:41
 * @VERSION：
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlerInfo {

    private String batchExecutorName;

    private int receiveCounter;

    private int failCounter;

    private int retryCounter;

    private List<ProcessorInfo> processorInfos;
}
