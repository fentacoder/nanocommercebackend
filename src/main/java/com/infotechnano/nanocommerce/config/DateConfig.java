package com.infotechnano.nanocommerce.config;

import com.infotechnano.nanocommerce.utils.DateHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateConfig {

    @Bean
    public DateHelper dateHelper(){
        return new DateHelper();
    }
}
