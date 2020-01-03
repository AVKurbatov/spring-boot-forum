package ru.avkurbatov_home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.avkurbatov_home.utils.Utils;

/**
 * Configuration for redis data source
 * */
@Configuration
@Profile("redis")
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer(){
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setDefaultSerializer(stringRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory());
        template.afterPropertiesSet();

        Utils.flushRedisDb(template);

        return template;
    }

}
