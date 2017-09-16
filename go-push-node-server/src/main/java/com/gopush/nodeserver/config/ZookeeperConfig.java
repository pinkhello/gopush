package com.gopush.nodeserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/11 下午11:44
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "go-push.zookeeper")
public class ZookeeperConfig {

    private String servers;

    private String namespace;

    private String listenNamespace;

    private int sessionTimeout;

    private int connectionTimeout;

    private int maxRetries;

    private int retriesSleepTime;
}
