package com.file.loader.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class StorageConfiguration {

    private String uploadDir;

    public StorageConfiguration(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public StorageConfiguration() {
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
