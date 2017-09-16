package com.gopush.nodeserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/11 下午11:33
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "go-push.node-server.batch-processor")
public class BatchProcessorConfig {
    /**
     * 批量处理的定时器延时
     */
    private int delay;
    /**
     * 批量处理的大小
     */
    private int batchSize;
    /**
     * 消息队列里面超过这个大小就要进行告警
     */
    private int warnThreshold;
    /**
     * 子处理器的个数
     */
    private int processorSize;

    /**
     * 不指定线程池的时候,指定初始化默认创建的线程池的大小
     */
    private int corePoolSize;

}
