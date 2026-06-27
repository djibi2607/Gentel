package com.abdoul.hotel.Config;

import com.abdoul.hotel.Filters.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilterFilterRegistrationBean (JwtFilter jwtFilter){
        FilterRegistrationBean<JwtFilter> reg = new FilterRegistrationBean<>();

        reg.setFilter(jwtFilter);
        reg.addUrlPatterns("/api/users/verify");
        reg.setOrder(1);

        return reg;
    }
}
