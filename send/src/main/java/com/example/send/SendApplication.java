package com.example.send;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.send.mapper")
public class SendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SendApplication.class, args);
    }
}
