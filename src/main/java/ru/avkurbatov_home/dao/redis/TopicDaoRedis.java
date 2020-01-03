package ru.avkurbatov_home.dao.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.jdo.Topic;
import ru.avkurbatov_home.utils.StringTypeConverter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Structure in redis:
 * TOPIC_IDS_COUNTER --- value with ids
 * TOPIC_IDS --- set with all available ids
 * TOPIC_HASH_PREFIX + ":id" --- hash with id and title
 * */
// todo: messages have links to topics, so method with delete topic should delete all his messages
@Slf4j
@Repository
@Profile("redis")
public class TopicDaoRedis implements TopicDao {

    private static final String TOPIC_IDS_COUNTER = "topicIdCount";
    private static final String TOPIC_IDS = "topicIds";
    private static final String TOPIC_HASH_PREFIX = "topic:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Inject
    public TopicDaoRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Topic> findAll() {
        List<Topic> topics = new ArrayList<>();
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                Set<String> ids = sc.sMembers(TOPIC_IDS);
                ids.forEach(id -> {
                    Integer intId = StringTypeConverter.toInteger(id);
                    if (intId == null){
                        return;
                    }
                    topics.add(Topic.fromRedisMap(sc.hGetAll(hashKey(intId))));
                });
                return null;
            }
        });
        return topics;
    }

    @Override
    public Topic findById(int id) {
        return Topic.fromRedisMap(
                redisTemplate.<String, String>opsForHash().entries(hashKey(id)));
    }

    @Override
    public Topic save(Topic topic) {
        int id = redisTemplate.opsForValue().increment(TOPIC_IDS_COUNTER, 1L).intValue();
        topic.setId(id);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                sc.sAdd(TOPIC_IDS, StringTypeConverter.fromInteger(id));
                sc.hMSet(hashKey(id), topic.buildRedisMap());
                return null;
            }
        });
        return topic;
    }

    private String hashKey(int id){
        return TOPIC_HASH_PREFIX + id;
    }
}
