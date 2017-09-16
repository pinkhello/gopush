package com.gopush.infos.nodeserver.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * go-push
 *
 * @类功能说明：每个批处理机内部处理器 信息
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/20 下午8:37
 * @VERSION：
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorInfo {

    private String batchName;

    private int index;

    private int loader;

}
