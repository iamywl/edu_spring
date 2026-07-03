package com.edu.intro;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DI(의존성 주입)를 통해 느슨한 결합을 구현하는 예제
@RestController
@RequestMapping("/api")
public class HelloController {

    // 생성자 주입 (권장 방식)
    private final GreetingService greetingService;
    private final DateTimeFormatter dateTimeFormatter;

    public HelloController(GreetingService greetingService, DateTimeFormatter dateTimeFormatter) {
        this.greetingService = greetingService;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return greetingService.greet(name);
    }

    @GetMapping("/time")
    public String currentTime() {
        return "현재 시간: " + LocalDateTime.now().format(dateTimeFormatter);
    }
}
