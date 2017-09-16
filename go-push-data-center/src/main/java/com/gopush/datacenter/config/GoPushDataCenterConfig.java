package com.gopush.datacenter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * go-push
 *
 * @类功能说明：
 * @作者：喝咖啡的囊地鼠
 * @创建时间：2017/6/21 下午1:28
 * @VERSION：
 */
@Configuration
@ConfigurationProperties(prefix = "go-push.data-center")
@Data
public class GoPushDataCenterConfig {
    private String name;
}
