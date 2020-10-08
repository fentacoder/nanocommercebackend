package com.infotechnano.nanocommerce.config;

import com.infotechnano.nanocommerce.utils.StripeUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Bean
    public StripeUtil stripeUtil(){
        return new StripeUtil();
    }
}
