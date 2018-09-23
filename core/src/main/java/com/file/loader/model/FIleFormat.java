package com.file.loader.model;

public enum FIleFormat {

    PNG("image/png"),
    JPG("image/jpg"),
    JPEG("image/jpeg");

    private String format;

    FIleFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
