package com.instituto.compendium.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadDirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadPath = uploadDirPath.toFile().getAbsolutePath();
        String fallbackPath = Paths.get(System.getProperty("java.io.tmpdir"), "uploads")
            .toAbsolutePath()
            .normalize()
            .toString();
        
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(
                "file:" + uploadPath + "/",
                "file:" + fallbackPath + "/"
            );
    }
}