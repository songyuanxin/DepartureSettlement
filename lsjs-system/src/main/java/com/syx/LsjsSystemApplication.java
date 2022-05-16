package com.syx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)
public class LsjsSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LsjsSystemApplication.class, args);
        System.out.println("离司结算服务启动成功！");
    }

}
