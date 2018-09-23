package com.file.loader.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {
        "classpath:config/${spring.profiles.active}/loader.properties",
        "file:${app.properties.override.path}/loader.properties"
}, ignoreResourceNotFound = true)
@EnableConfigurationProperties({
        StorageConfiguration.class
})
public class PropertiesConfiguration {
}
