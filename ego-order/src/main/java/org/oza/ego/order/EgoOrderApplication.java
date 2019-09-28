package org.oza.ego.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient //启动eureka客户端支持
public class EgoOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgoOrderApplication.class, args);
    }

}
