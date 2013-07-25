package com.perfectworldprogramming.mod.spring.app.context.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: Mark Spritzler
 * Date: 7/14/13
 * Time: 10:53 AM
 */
@Configuration
public class AnotherJavaConfiguration {

    @Bean
    public String goodbyeWorld() {
        return "GoodBye Cruel World";
    }
}
