package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello! My first Jenkins CI/CD Java microservice is running.";
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
