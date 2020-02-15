package ru.avkurbatov_home.dao.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.avkurbatov_home.dao.abstracts.MessageDao;
import ru.avkurbatov_home.jdo.Message;
import ru.avkurbatov_home.utils.StringTypeConverter;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;

import static ru.avkurbatov_home.dao.redis.StructureNames.*;

/**
 * Structure in redis:
 * MESSAGE_IDS_COUNTER --- value with ids
 * MESSAGE_IDS --- set with all available ids
 * MESSAGE_HASH_PREFIX + ":id" --- hash with id and title
 *
 * TOPIC_IDS_WITH_MESSAGES_SET --- set with all topicIds with messages
 * MESSAGE_IDS_IN_TOPIC_PREFIX + ":topicId" --- set with all ids in this topic
 *
 * ACCOUNT_IDS_WITH_MESSAGES_SET --- set with all usernames with messages
 * MESSAGE_IDS_IN_ACCOUNT_PREFIX + ":username" --- set with all message ids made by this user
 *
 * */
@Slf4j
@Repository
@Profile("redis")
public class MessageDaoRedis implements MessageDao {

    private final int PAGE_SIZE;
    private static final MessageComparator messageComparator = new MessageComparator();

    private final RedisTemplate<String, Object> redisTemplate;

    @Inject
    public MessageDaoRedis(@Value("${message.dao.page.size}") int pageSize,
                            RedisTemplate<String, Object> redisTemplate) {
        this.PAGE_SIZE = pageSize;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Message> findForPage(int topicId, int pageNumber) {
        List<Message> messages = new ArrayList<>();

        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                Set<String> ids = sc.sMembers(messagesInTopicSetKey(topicId));
                ids.forEach(id -> {
                    Integer intId = StringTypeConverter.toInteger(id);
                    if (intId == null){
                        return;
                    }
                    Message message = findById(sc, intId);
                    if (message != null) {
                        messages.add(message);
                    }
                });
                return null;
            }
        });

        int from = pageNumber * PAGE_SIZE;
        int to = (pageNumber + 1) * PAGE_SIZE;
        int size = messages.size();

        if (from >= size) {
            return Collections.emptyList();
        }
        if (to > size) {
            to = size;
        }

        messages.sort(messageComparator);

        return messages.subList(from, to);
    }

    @Override
    public int findTotalNumberOfPages(int topicId) {
        Long numberOfMessages = redisTemplate.opsForSet().size(messagesInTopicSetKey(topicId));
        if (numberOfMessages == null) {
            throw new IllegalArgumentException("TopicId " + topicId + " is absent in database");
        }
        return (int)Math.ceil((double)numberOfMessages.intValue() / PAGE_SIZE);
    }

    @Override
    public Message save(Message message) {
        int id = redisTemplate.opsForValue().increment(MESSAGE_IDS_COUNTER, 1L).intValue();
        message.setId(id);
        Integer topicId = message.getTopicId();
        String username = message.getAccountUsername();
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                sc.sAdd(MESSAGE_IDS, StringTypeConverter.fromInteger(id));
                sc.hMSet(messageHashKey(id), message.buildRedisMap());
                if (topicId != null) {
                    sc.sAdd(TOPIC_IDS_WITH_MESSAGES_SET, StringTypeConverter.fromInteger(topicId));
                    sc.sAdd(messagesInTopicSetKey(topicId), StringTypeConverter.fromInteger(id));
                }
                if (username != null) {
                    sc.sAdd(ACCOUNT_IDS_WITH_MESSAGES_SET, username);
                    sc.sAdd(messagesInAccountSetKey(username), StringTypeConverter.fromInteger(id));
                }
                return null;
            }
        });
        return message;
    }

    @Override
    public void delete(int id) {
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                final StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                sc.sRem(MESSAGE_IDS, StringTypeConverter.fromInteger(id));
                final Message message = findById(sc, id);
                if (message == null) {
                    return null;
                }
                sc.del(messageHashKey(id));
                final Integer topicId = message.getTopicId();
                if (topicId != null) {
                    removeMessageFromTopicSet(sc, id, topicId);
                }
                final String username = message.getAccountUsername();
                if (username != null) {
                    removeMessageFromAccountSet(sc, id, username);
                }
                return null;
            }
        });
    }

    private void removeMessageFromTopicSet(StringRedisConnection sc, int id, Integer topicId) {
        sc.sRem(messagesInTopicSetKey(topicId), StringTypeConverter.fromInteger(id));
        if (!sc.exists(messagesInTopicSetKey(topicId))) {
            sc.sRem(TOPIC_IDS_WITH_MESSAGES_SET, StringTypeConverter.fromInteger(topicId));
        }
    }

    private void removeMessageFromAccountSet(StringRedisConnection sc, int id, String username) {
        sc.sRem(messagesInAccountSetKey(username), StringTypeConverter.fromInteger(id));
        if (!sc.exists(messagesInAccountSetKey(username))) {
            sc.sRem(ACCOUNT_IDS_WITH_MESSAGES_SET, username);
        }
    }

    private static class MessageComparator implements Comparator<Message>{

        @Override
        public int compare(Message message1, Message message2) {
            LocalDateTime date1 = message1.getDate();
            LocalDateTime date2 = message2.getDate();
            int result = compareWithNullCheck(date1, date2);
            if (result != 0) {
                return result;
            }

            Integer id1 = message1.getId();
            Integer id2 = message2.getId();
            return compareWithNullCheck(id1, id2);
        }

        private <T> int compareWithNullCheck(Comparable<T> o1, T o2){
            if (o1 == null && o2 == null) return 0;
            if (o1 == null) return -1;
            if (o2 == null) return 1;

            return o1.compareTo(o2);
        }
    }

    private Message findById(StringRedisConnection sc, int id) {
        return Message.fromRedisMap(sc.hGetAll(messageHashKey(id)));
    }

}
