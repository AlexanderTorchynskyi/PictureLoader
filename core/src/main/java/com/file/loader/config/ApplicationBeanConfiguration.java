package com.file.loader.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class ApplicationBeanConfiguration {

    @Bean
    public RestTemplate createrestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(1000 * 30)
                .setReadTimeout(1000 * 30)
                .build();
    }
}
