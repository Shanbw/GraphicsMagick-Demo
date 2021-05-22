package com.shan.image.config;

import lombok.Data;
import org.gm4java.engine.support.WhenExhaustedAction;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GM 进程池参数
 */
@ConfigurationProperties(prefix = "gm.pool")
@Data
public class GMPoolProperties {

    /**
     * 连接池最大活跃数
     */
    private int maxActive = 4;

    /**
     * 连接池最大空闲连接数
     */
    private int maxIdle = 4;

    /**
     * 连接池最小空闲连接数
     */
    private int minIdle = 2;

    /**
     * 资源池中资源最小空闲时间(单位为毫秒)，达到此值后空闲资源将被移
     */
    private long minEvictableIdleTimeMillis = 300000L;

    /**
     * 连接池连接用尽后执行的动作
     */
    private WhenExhaustedAction whenExhaustedAction = WhenExhaustedAction.BLOCK;

    /**
     * 连接池没有对象返回时，最大等待时间(毫秒)
     */
    private long maxWait = 5000;

    /**
     * 定时对线程池中空闲的链接进行校验
     */
    private boolean testWhileIdle = false;

    /**
     * 空闲资源的检测周期(单位为毫秒)
     */
    private long timeBetweenEvictionRunsMillis = 10000L;

}
