package com.gopush.protocol.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * go-push
 *
 * @类功能说明：节点运行信息请求
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/9
 * @VERSION：
 */
@Builder
@Data
@AllArgsConstructor
public class NodeInfoReq extends NodeMessageReq<NodeInfoReq> {
    @Override
    protected Type type() {
        return Type.NI;
    }

    @Override
    protected NodeInfoReq getThis() {
        return this;
    }


}
