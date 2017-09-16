package com.gopush.monitor.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 下午5:15
 */
@Controller
@Api(tags = "监控中心")
public class MonitorController {

    @ApiOperation("首页跳转")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "redirect:/monitor";
    }

    @ApiOperation("监控中心")
    @RequestMapping(value = "/monitor", method = RequestMethod.GET)
    public String monitor() {
        return "monitor";
    }
}
