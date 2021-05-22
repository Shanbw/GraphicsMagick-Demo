package com.shan.image.demo;

import com.shan.image.BaseTest;
import org.gm4java.engine.support.PooledGMService;
import org.im4java.core.IMOperation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;

public class DemoTest extends BaseTest {

    @Autowired
    private PooledGMService pooledGMService;

    @Test
    public void resizeTest() throws Exception {
        File distImg = File.createTempFile("dist-", ".jpg");
        IMOperation op = new IMOperation();
        op.addRawArgs("convert");
        op.resize(100, 100)
                .addImage(sourceImgPath)
                .addImage(distImg.getAbsolutePath());
        System.out.println("distPath: "+distImg.getAbsolutePath());
        pooledGMService.execute(op.getCmdArgs());
    }

}
