package com.zooManager.zooManager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String uploadPath = Paths.get(System.getProperty("user.dir"),"uploads").toFile().getAbsolutePath();


                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + uploadPath + "/");
                registry.addResourceHandler("/**")
                        .addResourceLocations("classpath:/static/");
    }
}
