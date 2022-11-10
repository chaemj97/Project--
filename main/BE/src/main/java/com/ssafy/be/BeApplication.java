package com.ssafy.be;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BeApplication {


    public static void main(String[] args) {
        SpringApplication.run(BeApplication.class, args);

    }

}
