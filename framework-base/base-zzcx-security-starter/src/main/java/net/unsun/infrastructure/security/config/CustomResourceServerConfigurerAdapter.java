package net.unsun.infrastructure.security.config;

import net.unsun.infrastructure.security.ex.handler.UserAccessDeniedHandler;
import net.unsun.infrastructure.security.ex.handler.UserAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.cors.CorsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-22 15:59
 */
@Order(2)
@Configuration
@EnableResourceServer
//需要重写时排除
@ConditionalOnMissingBean(ResourceServerConfigurerAdapter.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter {

    @Autowired
    CustomSecurityProperties customSecurityProperties;

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint(){
        return new UserAuthenticationEntryPoint();
    }

    @Bean
    UserAccessDeniedHandler userAccessDeniedHandler(){
        return new UserAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Autowired(required = false)
    AccessDecisionManager accessDecisionManager;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.authenticationEntryPoint(authenticationEntryPoint()).accessDeniedHandler(userAccessDeniedHandler());
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        UserAuthenticationConverter userTokenConverter = new CustomUserAuthenticationConverter();
        accessTokenConverter.setUserTokenConverter(userTokenConverter);
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(customSecurityProperties.getCheckTokenUrl());
        tokenService.setClientId(customSecurityProperties.getClientId());
        tokenService.setClientSecret(customSecurityProperties.getClientSecret());
        tokenService.setAccessTokenConverter(accessTokenConverter);
        resources.tokenServices(tokenService).resourceId(customSecurityProperties.getResourceId());
    }



    @Override
    public void configure(HttpSecurity http) throws Exception {
        //跨域
        http
                .cors()
                .and()
                .csrf().disable();
        if(customSecurityProperties.getPathPassPattern() != null) {
            List<String> pathPassPatterns = customSecurityProperties.getPathPassPattern();
            List<String> patterns = new ArrayList<>();
            if(pathPassPatterns != null && pathPassPatterns.size() >0) {
                for (String passPattern : pathPassPatterns) {
                    patterns.addAll(Arrays.asList(passPattern.split(",")));
                }
                String[] pathes = patterns.toArray(new String[patterns.size()]);
                http.authorizeRequests()
                        .antMatchers(pathes).permitAll();
            }

        }

        if(customSecurityProperties.getOpenToAddress() != null) {
            String[] pathAndAddress = customSecurityProperties.getOpenToAddress().split(":");
            http.authorizeRequests()
                    .antMatchers(pathAndAddress[0]).hasIpAddress(pathAndAddress[1]);
        }

        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest)
                .permitAll()
                .anyRequest().authenticated();

        //如果业务项目有 accessDecisionManager，则注入
        if(accessDecisionManager != null) {
            http.authorizeRequests().withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {       // 重写做权限判断
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                    o.setAccessDecisionManager(accessDecisionManager);      // 权限判断
                    return o;
                }
            });
        }
    }

}