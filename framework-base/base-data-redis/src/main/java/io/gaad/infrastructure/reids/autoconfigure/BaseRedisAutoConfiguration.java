package io.gaad.infrastructure.reids.autoconfigure;

import io.gaad.infrastructure.reids.config.RedisConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @program: framework
 * @author: Tokey
 * @create: 2019-12-10 19:10
 */
@Import(RedisConfig.class)
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class BaseRedisAutoConfiguration {
}