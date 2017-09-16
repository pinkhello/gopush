package com.gopush.datacenter.restfuls.controller;

import com.gopush.datacenter.restfuls.pojo.BaseResp;
import com.gopush.datacenter.restfuls.pojo.bo.Device;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 下午10:57
 */


@RestController
@RequestMapping(value = "/apis/push")
@Api(tags = "推送消息相关")
public class ApisPushController {

    @ApiOperation(value = "向单个设备推送消息", notes = "向单个设备推送消息")
    @RequestMapping(value = "/one", method = RequestMethod.POST)
    public ResponseEntity<BaseResp> pushOne(@RequestBody Device device) {
        //todo
        return ResponseEntity.ok(BaseResp.ok());
    }

    @ApiOperation(value = "向多个设备推送消息", notes = "向多个设备推送消息")
    @RequestMapping(value = "/numerous", method = RequestMethod.POST)
    public ResponseEntity<BaseResp> pushNumerous(@RequestBody Device device) {
        //todo
        return ResponseEntity.ok(BaseResp.ok());
    }
}
