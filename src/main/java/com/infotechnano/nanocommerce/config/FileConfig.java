package com.infotechnano.nanocommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FileConfig {

    @Bean
    public MultipartResolver multipartResolver(){
        return new CustomCommonsMultipartResolver();
    }

    public static class CustomCommonsMultipartResolver extends CommonsMultipartResolver {
        @Override
        public boolean isMultipart(HttpServletRequest request) {
            final String header = request.getHeader("Content-Type");
            if(header == null){
                return false;
            }
            return header.contains("multipart/form-data");
        }
    }
}
