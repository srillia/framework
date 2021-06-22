package io.gaad.infrastructure.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-28 16:20
 */
@ConfigurationProperties(prefix = "security.oauth2.custom")
public class CustomSecurityProperties {

    /**
     * 验证请求
     */
    String checkTokenUrl;

    /**
     * 用户id
     */
    String clientId;

    /**
     * 客户密码
     */
    String clientSecret;

    /**
     * 配置资源id
     */
    String resourceId;

    /**
     * 开放路径
     */
    List<String> pathPassPattern;

    /**
     * 开放给具体的ip
     */
    String openToAddress;

    public String getCheckTokenUrl() {
        return checkTokenUrl;
    }

    public void setCheckTokenUrl(String checkTokenUrl) {
        this.checkTokenUrl = checkTokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<String> getPathPassPattern() {
        return pathPassPattern;
    }

    public void setPathPassPattern(List<String> pathPassPattern) {
        this.pathPassPattern = pathPassPattern;
    }

    public String getOpenToAddress() {
        return openToAddress;
    }

    public void setOpenToAddress(String openToAddress) {
        this.openToAddress = openToAddress;
    }
}