package com.file.loader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;


@SpringBootApplication

public class ApplicationLauncher extends SpringBootServletInitializer {

    public static void main(String[] args) {
        String activeProfiles = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
        if (Objects.isNull(activeProfiles) || activeProfiles.isEmpty()) {
            throw new RuntimeException("Profile is not configured! Please setup an active profile!");
        }
        SpringApplication.run(ApplicationLauncher.class, args);
    }
}

