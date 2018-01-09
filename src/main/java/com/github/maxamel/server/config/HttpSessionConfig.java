package com.github.maxamel.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@Configuration
@EnableJdbcHttpSession 
public class HttpSessionConfig extends WebSecurityConfigurerAdapter{

        @Bean
        public HttpSessionStrategy httpSessionStrategy() {
                return new HeaderHttpSessionStrategy(); 
        }
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .requestCache()
                    .requestCache(new NullRequestCache())
                    .and()
                    .httpBasic();
        }
}