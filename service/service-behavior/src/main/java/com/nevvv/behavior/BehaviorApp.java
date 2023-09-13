package com.nevvv.behavior;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nevvv.user.Mapper")
public class BehaviorApp {
    public static void main(String[] args) {
        SpringApplication.run(BehaviorApp.class, args);
    }
}
