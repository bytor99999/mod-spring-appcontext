package com.perfectworldprogramming.mod.spring.app.context.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: Mark Spritzler
 * Date: 7/9/13
 * Time: 4:50 PM
 */
@Configuration
public class TestJavaConfiguration {

    @Bean
    public String helloWorld() {
        return "Hello World";
    }
}
