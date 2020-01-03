package ru.avkurbatov_home.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.avkurbatov_home.dao.abstracts.AccountDao;
import ru.avkurbatov_home.dao.abstracts.MessageDao;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.dao.redis.AccountDaoRedis;
import ru.avkurbatov_home.dao.redis.MessageDaoRedis;
import ru.avkurbatov_home.dao.redis.TopicDaoRedis;
import ru.avkurbatov_home.servers.TestRedisServer;


@TestConfiguration
@PropertySources({@PropertySource("localtest.properties")})
public class RedisDaoTestConfig {

    @Value("${redis.embedded.server.port}")
    private int port;

    @Bean
    public TestRedisServer redisServer(){
        return new TestRedisServer(port);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setPort(port);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
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

        return template;
    }

    @Bean
    public AccountDao accountDao() {
        return new AccountDaoRedis(redisTemplate());
    }

    @Bean
    public TopicDao topicDao() {
        return new TopicDaoRedis(redisTemplate());
    }

    @Bean
    public MessageDao messageDao(@Value("${message.dao.page.size}") int pageSize) {
        return new MessageDaoRedis(pageSize, redisTemplate());
    }
}
