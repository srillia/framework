package net.unsun.infrastructure.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-22 15:59
 */
@Order(2)
@Configuration
@EnableResourceServer
public class CustomResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter {

    @Autowired
    CustomSecurityProperties customSecurityProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
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
        http.csrf().disable();

        if(customSecurityProperties.getPathPassPattern() != null) {
            String[] passPatterns = customSecurityProperties.getPathPassPattern().split(",");
            http.authorizeRequests()
                    .antMatchers(passPatterns).permitAll();
        }

        if(customSecurityProperties.getOpenToAddress() != null) {
            String[] pathAndAddress = customSecurityProperties.getOpenToAddress().split(":");
            http.authorizeRequests()
                    .antMatchers(pathAndAddress[0]).hasIpAddress(pathAndAddress[1]);
        }

        http.authorizeRequests()
                .anyRequest().authenticated();

    }

}