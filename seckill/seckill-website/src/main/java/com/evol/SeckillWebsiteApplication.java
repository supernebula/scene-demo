package com.evol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.evol.dao.mapper"})
public class SeckillWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillWebsiteApplication.class, args);
    }

}
