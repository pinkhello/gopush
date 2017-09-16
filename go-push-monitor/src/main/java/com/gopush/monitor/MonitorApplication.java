package com.gopush.monitor;

import com.didispace.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * go-push
 *
 * @类功能说明：监控中心
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/10 上午4:43
 * @VERSION：
 */
@EnableSwagger2Doc
@SpringBootApplication
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }
}
