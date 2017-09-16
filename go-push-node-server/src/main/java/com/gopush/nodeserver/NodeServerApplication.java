package com.gopush.nodeserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/11 上午11:43
 * @VERSION：
 */
@EnableAsync
@SpringBootApplication
public class NodeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodeServerApplication.class, args);
    }


}
