package com.apsit.alumni.connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.apsit.alumni.connect.model")
public class ApsitConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApsitConnectApplication.class, args);
    }

}