package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoggingTestApplication {


    public static void main(String[] args) {
        SpringApplication.run(LoggingTestApplication.class, args);
        LoggingTest lt = new LoggingTest();
        lt.testLogging();
        System.out.println("Logging test complete");
    }
}
