package net.unsun.infrastructure.config.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: jshyun
 * @author: Tokey
 * @create: 2019-11-16 09:48
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "base.config")
public class ConfigProperties {

}
