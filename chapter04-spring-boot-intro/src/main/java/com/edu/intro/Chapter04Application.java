package com.edu.intro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
public class Chapter04Application {
    public static void main(String[] args) {
        SpringApplication.run(Chapter04Application.class, args);
    }
}
