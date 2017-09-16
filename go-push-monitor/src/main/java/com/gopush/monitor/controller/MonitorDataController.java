package com.gopush.monitor.controller;

import com.gopush.infos.datacenter.bo.DataCenterInfo;
import com.gopush.infos.nodeserver.bo.NodeServerInfo;
import com.gopush.monitor.controller.pojo.BaseResp;
import com.gopush.monitor.dymic.discovery.MonitorDataCenterService;
import com.gopush.monitor.dymic.discovery.MonitorNodeServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 下午5:09
 */
@RestController
@RequestMapping("/monitor")
@Api(tags = "监控数据")
public class MonitorDataController {

    @Autowired
    private MonitorDataCenterService monitorDataCenterService;

    @Autowired
    private MonitorNodeServerService monitorNodeServerService;

    @ApiOperation(value = "数据中心监控", notes = "DataCenter 数据中心 监控数据")
    @RequestMapping(value = "/dc", method = RequestMethod.GET)
    public ResponseEntity<BaseResp<List<DataCenterInfo>>> dataCenterInfos() {
        return ResponseEntity.ok(BaseResp.ok(monitorDataCenterService.dataCenterLoader()));
    }

    @ApiOperation(value = "节点服务监控", notes = "NodeServer 节点服务 监控数据")
    @RequestMapping(value = "/node", method = RequestMethod.GET)
    public ResponseEntity<BaseResp<List<NodeServerInfo>>> nodeServceInfos() {
        return ResponseEntity.ok(BaseResp.ok(monitorNodeServerService.nodeServerLoader()));
    }
}
