package ru.avkurbatov_home.dao.redis;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.avkurbatov_home.config.RedisDaoTestConfig;
import ru.avkurbatov_home.dao.abstracts.TopicDaoTest;
import ru.avkurbatov_home.utils.Utils;

import javax.inject.Inject;

@Import(RedisDaoTestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TopicDaoRedisTest extends TopicDaoTest {

    @Inject
    public RedisTemplate<String, Object> redisTemplate;

    @Before
    public void init(){
        Utils.flushRedisDb(redisTemplate);
    }

}