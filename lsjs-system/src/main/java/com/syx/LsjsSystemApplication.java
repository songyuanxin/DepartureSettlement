package com.syx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableCaching
@MapperScan(basePackages = {"com.syx.mapper.lsjs", "com.syx.mapper.SAPStoreHead"})
public class LsjsSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LsjsSystemApplication.class, args);
        System.out.println("离司结算服务启动成功！");
    }

}
