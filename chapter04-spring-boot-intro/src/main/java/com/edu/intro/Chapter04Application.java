package com.edu.intro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
@SpringBootApplication
// @EnableConfigurationProperties: @ConfigurationProperties 클래스(AppProperties)를 Bean으로 등록한다
@EnableConfigurationProperties(AppProperties.class)
public class Chapter04Application {
    public static void main(String[] args) {
        SpringApplication.run(Chapter04Application.class, args);
    }
}
