package com.example.baro_15;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Baro15Application {

    public static void main(String[] args) {
        SpringApplication.run(Baro15Application.class, args);
    }

}
