package com.janne.dreialbenapiproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DreiAlbenApiProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DreiAlbenApiProxyApplication.class, args);
    }

}
