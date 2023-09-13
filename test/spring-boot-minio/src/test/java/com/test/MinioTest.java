package com.test;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
public class MinioTest {
    @Autowired
    private MinioService minioService;

    @Test
    public void test01() throws FileNotFoundException, MinioException {
        Path path= Paths.get("mis.jpg");
        InputStream file=new FileInputStream("D:\\夜小雨\\图片\\1054256.jpg");
        minioService.upload(path,file, ContentType.IMAGE_JPEG);
    }
    @Test
    public void test02() throws FileNotFoundException, MinioException {
        Path path= Paths.get("mis.jpg");
        InputStream file=new FileInputStream("D:\\夜小雨\\图片\\1054256.jpg");
        minioService.getAndSave(path,"d:\\5.jpg");
    }
    @Test
    public void test03() throws Exception {
        InputStream inputStream = minioService.get(Paths.get("mis.jpg"));
        Files.copy(inputStream,Paths.get("d:\\b.jpg"));
    }
}
