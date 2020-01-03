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

/**
 * Structure in redis:
 * MESSAGE_IDS_COUNTER --- value with ids
 * MESSAGE_IDS --- set with all available ids
 * MESSAGE_HASH_PREFIX + ":id" --- hash with id and title
 *
 * TOPIC_IDS_WITH_MESSAGES_SET --- set with all topicIds with messages
 * MESSAGE_IDS_FOR_TOPIC_PREFIX + ":topicId" --- set with all ids in this topic
 *
 * ACCOUNT_IDS_WITH_MESSAGES_SET --- set with all usernames with messages
 * MESSAGE_IDS_FOR_ACCOUNT_PREFIX + ":username" --- set with all usernames in this topic
 *
 * */
@Slf4j
@Repository
@Profile("redis")
public class MessageDaoRedis implements MessageDao {
    private static final String MESSAGE_IDS_COUNTER = "messageIdsCount";
    private static final String MESSAGE_IDS = "messages";
    private static final String MESSAGE_HASH_PREFIX = "message:";
    private static final String TOPIC_IDS_WITH_MESSAGES_SET = "messagesForTopics";
    private static final String MESSAGE_IDS_FOR_TOPIC_PREFIX = "messagesForTopic:";
    private static final String ACCOUNT_IDS_WITH_MESSAGES_SET = "messagesForAccounts";
    private static final String MESSAGE_IDS_FOR_ACCOUNT_PREFIX = "messagesForAccount:";

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
                Set<String> ids = sc.sMembers(topicsSetKey(topicId));
                ids.forEach(id -> {
                    Integer intId = StringTypeConverter.toInteger(id);
                    if (intId == null){
                        return;
                    }
                    messages.add(Message.fromRedisMap(sc.hGetAll(hashKey(intId))));
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
        Long numberOfMessages = redisTemplate.opsForSet().size(topicsSetKey(topicId));
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
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                sc.sAdd(MESSAGE_IDS, StringTypeConverter.fromInteger(id));
                sc.hMSet(hashKey(id), message.buildRedisMap());
                if (topicId != null) {
                    sc.sAdd(TOPIC_IDS_WITH_MESSAGES_SET, StringTypeConverter.fromInteger(topicId));
                    sc.sAdd(topicsSetKey(topicId), StringTypeConverter.fromInteger(id));
                }
                if (username != null) {
                    sc.sAdd(ACCOUNT_IDS_WITH_MESSAGES_SET, username);
                    sc.sAdd(accountSetKey(username), StringTypeConverter.fromInteger(id));
                }
                return null;
            }
        });
        return message;
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

    private String hashKey(int id) {
        return MESSAGE_HASH_PREFIX + id;
    }

    private String topicsSetKey(int topicId){
        return MESSAGE_IDS_FOR_TOPIC_PREFIX + topicId;
    }

    private String accountSetKey(String username){
        return MESSAGE_IDS_FOR_ACCOUNT_PREFIX + username;
    }
}
