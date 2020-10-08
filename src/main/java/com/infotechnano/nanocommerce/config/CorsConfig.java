package com.infotechnano.nanocommerce.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig implements Filter {

    private final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    public CorsConfig(){
        log.info("CorsConfig init");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        System.out.println("request cookies: " + Arrays.toString(request.getCookies()));
        System.out.println("WebConfig; "+request.getRequestURI());
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS,PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,observe, X-XSRF-TOKEN, *");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.addHeader("Access-Control-Expose-Headers", "USERID");
        response.addHeader("Access-Control-Expose-Headers", "ROLE");
        response.addHeader("Access-Control-Expose-Headers", "responseType");
        response.addHeader("Access-Control-Expose-Headers", "observe");
        response.addHeader("Access-Control-Expose-Headers", "*");
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("Request Method: "+request.getMethod() + " " + response.getStatus());
        if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
            try {
                chain.doFilter(request, response);
                System.out.println("CorsConfig: response end: " + response.getHeader("Access-Control-Allow-Headers") + " " + response.getStatus());
            } catch(Exception e) {
                e.printStackTrace();

            }
        } else {
            System.out.println("Pre-flight");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS,PUT, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers","Access-Control-Allow-Headers, Access-Control-Allow-Origin, Origin, Authorization,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, x-xsrf-token, access-control-allow-methods, access-control-allow-origin, access-control-allow-credentials, X-XSRF-TOKEN");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        //System.out.println("CorsConfig: response end: " + response.getHeader("Access-Control-Allow-Headers") + " " + response.getStatus());
    }
}
