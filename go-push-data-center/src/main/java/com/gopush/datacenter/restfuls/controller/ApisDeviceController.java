package com.gopush.datacenter.restfuls.controller;

import com.gopush.common.constants.IdleEnum;
import com.gopush.datacenter.dymic.discovery.NodeServerDiscoveryService;
import com.gopush.datacenter.restfuls.pojo.BaseResp;
import com.gopush.datacenter.restfuls.pojo.bo.Device;
import com.gopush.datacenter.restfuls.pojo.bo.LoadbanceNode;
import com.gopush.infos.nodeserver.bo.NodeServerInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 下午3:18
 */

@RestController
@RequestMapping(value = "/apis/device")
@Api(tags = "设备|链接相关")
public class ApisDeviceController {

    @Autowired
    private NodeServerDiscoveryService nodeServerDiscoveryService;

    @ApiOperation(value = "设备注册", notes = "注册设备（一台设备只注册一次，但是可以增加APPCode）")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<BaseResp<String>> register(@RequestBody Device device) {
        //内部生产一个设备号给注册的设备
        //查找缓存，设置注册设备，没有设备的话注册，有的话检查appcode，没有的加入appcode 有的话直接返回已经注册的设备号
        //todo
        return ResponseEntity.ok(BaseResp.ok(token()));
    }

    @ApiOperation(value = "设备取消注册", notes = "取消注册设备（一台设备只注册一次）")
    @RequestMapping(value = "/unregister/{deviceNo}", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResp> unregister(@PathVariable("deviceNo") @ApiParam("设备号") String deviceNo) {
        //查找缓存，没有设备的话直接返回，有的话 取消注册的设备，清空设备的有效期（从而导致sdk端或者服务端关闭链接）
        //todo
        return ResponseEntity.ok(BaseResp.ok());
    }

    @ApiOperation(value = "查询设备状态", notes = "查询设备状态")
    @RequestMapping(value = "/{deviceNo}/state", method = RequestMethod.GET)
    public ResponseEntity<BaseResp> deviceState(@PathVariable("deviceNo") @ApiParam("设备号") String deviceNo) {
        //查找缓存，没有设备的话直接返回，有的话 取消注册的设备，清空设备的有效期（从而导致sdk端或者服务端关闭链接）
        //todo 查询设备状态
        return ResponseEntity.ok(BaseResp.ok());
    }


    @ApiOperation(value = "设备选择链接节点", notes = "设备选择链接节点")
    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public ResponseEntity<BaseResp<LoadbanceNode>> selectNode() {
        Map<String, NodeServerInfo> maps = new HashMap<>(nodeServerDiscoveryService.nodeServerPool());
        if (!CollectionUtils.isEmpty(maps)) {
            NodeServerInfo info = maps.values().stream()
                    .min(Comparator.comparingInt(e -> e.getNodeLoaderInfo().getOnlineDeviceCounter()))
                    .get();
            return ResponseEntity.ok(
                    BaseResp.ok(
                            LoadbanceNode.builder()
                                    .ip(info.getInternetIp())
                                    .port(info.getDevicePort())
                                    .readInterval(IdleEnum.READ_IDLE.getValue())
                                    .writeInterval(IdleEnum.WRITE_IDLE.getValue())
                                    .allInterval(IdleEnum.ALL_IDLE.getValue())
                                    .build()
                    ));
        }
        return ResponseEntity.ok(BaseResp.fail(400, "无可链接节点"));
    }


    private String token() {
        return UUID.randomUUID().toString().replaceAll("\\-", "");
    }
}
