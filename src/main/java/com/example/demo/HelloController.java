package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "return "Hello! Version 2 is deployed automatically!";.";
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}