package com.shan.image.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.gm4java.engine.support.GMConnectionPoolConfig;
import org.gm4java.engine.support.PooledGMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * GM配置
 */
@EnableConfigurationProperties(GMPoolProperties.class)
@Slf4j
@Configuration
public class GMConfig {

    private final GMPoolProperties gmPoolProperties;

    private String GM_PATH;

    @Autowired
    public GMConfig(GMPoolProperties gmPoolProperties) {
        this.gmPoolProperties = gmPoolProperties;
    }

    @PostConstruct
    private void initGM() throws Exception {
        String osName = System.getProperty("os.name").toLowerCase();
        log.info("os name: {}", osName);
        String gmPath;
        if (osName.contains("mac")) {
            gmPath = "gm/mac/gm";
        } else if (osName.contains("linux")) {
            initPodEnv();
            gmPath = "gm/linux/gm";
        } else {
            throw new RuntimeException("非法操作系统："+osName);
        }
        InputStream fisInJar = new ClassPathResource(gmPath).getInputStream();
        File file = File.createTempFile("GraphicsMagick", "_gm");
        file.setExecutable(true);
        GM_PATH = file.getAbsolutePath();
        //将jar包里的gm复制到操作系统的目录里
        OutputStream fosInOs = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int readLength = fisInJar.read(buffer);
        while (readLength != -1) {
            fosInOs.write(buffer, 0, readLength);
            readLength = fisInJar.read(buffer);
        }
        IOUtils.closeQuietly(fosInOs);
        IOUtils.closeQuietly(fisInJar);
        log.info("gm初始化完毕");
    }

    /**
     * 初始化容器的环境
     *
     * 安装gm所依赖的库
     */
    private void initPodEnv() throws Exception {
        log.info("============ start init pod env ============");
        Process exec1 = Runtime.getRuntime().exec("yum install -y gcc make");
        this.printLog(exec1);
        log.info("cmd 1 exec success");
        Process exec2 = Runtime.getRuntime().exec("yum install -y libpng-devel libjpeg-devel libtiff-devel jasper-devel freetype-devel libtool-ltdl-devel*");
        this.printLog(exec2);
        log.info("cmd 2 exec success");
        // 打水印时缺少依赖
        /*Process exec3 = Runtime.getRuntime().exec("yum -y install ghostscript");
        this.printLog(exec3);
        log.info("cmd 3 exec success");*/
        log.info("============ init pod env success ============");
    }

    /**
     * 输出安装库的日志
     */
    private void printLog(Process process) throws Exception {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String s;
        while ((s = stdInput.readLine()) != null) {
            log.info(s);
        }
        while ((s = stdError.readLine()) != null) {
            log.error(s);
        }
        process.waitFor();
    }

    /**
     * GM服务进程池
     */
    @Bean
    public PooledGMService getGMService() {
        GMConnectionPoolConfig config = new GMConnectionPoolConfig();
        // GM 可执行文件的路径
        config.setGMPath(GM_PATH);
        config.setMaxActive(gmPoolProperties.getMaxActive());
        config.setMaxIdle(gmPoolProperties.getMaxIdle());
        config.setMinIdle(gmPoolProperties.getMinIdle());
        config.setMinEvictableIdleTimeMillis(gmPoolProperties.getMinEvictableIdleTimeMillis());
        config.setWhenExhaustedAction(gmPoolProperties.getWhenExhaustedAction());
        config.setMaxWait(gmPoolProperties.getMaxWait());
        config.setTestWhileIdle(gmPoolProperties.isTestWhileIdle());
        config.setTimeBetweenEvictionRunsMillis(gmPoolProperties.getTimeBetweenEvictionRunsMillis());
        log.info("init gm pool finished");
        return new PooledGMService(config);
    }

}
