package net.unsun.infrastructure.reids.autoconfigure;

import net.unsun.infrastructure.reids.config.RedisConfig;
import org.springframework.context.annotation.Import;

/**
 * @program: framework
 * @author: Tokey
 * @create: 2019-12-10 19:10
 */
@Import(RedisConfig.class)
public class RedisAutoConfiguration {
}