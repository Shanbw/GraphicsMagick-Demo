package com.shan.image;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseTest {

    protected String sourceImgPath;

    @PostConstruct
    private void initSourceImg() throws Exception {
        InputStream fisInJar = new ClassPathResource("img/sourceImg.jpeg").getInputStream();
        File sourceImg = File.createTempFile("source-", ".jpeg");
        sourceImgPath = sourceImg.getAbsolutePath();
        //将jar包里的gm复制到操作系统的目录里
        OutputStream fosInOs = new FileOutputStream(sourceImg);
        byte[] buffer = new byte[1024];
        int readLength = fisInJar.read(buffer);
        while (readLength != -1) {
            fosInOs.write(buffer, 0, readLength);
            readLength = fisInJar.read(buffer);
        }
        IOUtils.closeQuietly(fosInOs);
        IOUtils.closeQuietly(fisInJar);
    }

}