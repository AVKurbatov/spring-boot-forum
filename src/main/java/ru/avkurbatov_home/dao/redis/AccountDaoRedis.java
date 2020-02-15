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
import ru.avkurbatov_home.dao.abstracts.AbstractAccountDao;
import ru.avkurbatov_home.dao.abstracts.MessageDao;
import ru.avkurbatov_home.enums.RegisterResult;
import ru.avkurbatov_home.jdo.Account;
import org.apache.commons.lang3.mutable.MutableBoolean;
import ru.avkurbatov_home.utils.StringTypeConverter;

import javax.inject.Inject;
import java.util.*;

import static ru.avkurbatov_home.dao.redis.StructureNames.*;
import static ru.avkurbatov_home.dao.redis.StructureNames.TOPIC_IDS;
import static ru.avkurbatov_home.dao.redis.StructureNames.topicHashKey;

/**
 * Structure in redis:
 * ACCOUNT_USERNAMES --- set with all available usernames
 * ACCOUNT_PREFIX + ":username" --- hash with id and title
 * ACCOUNT_AUTHORITIES + ":username" --- set with authorities
 * */
@Slf4j
@Repository
@Profile("redis")
public class AccountDaoRedis extends AbstractAccountDao {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageDao messageDao;

    @Inject
    public AccountDaoRedis(RedisTemplate<String, Object> redisTemplate, MessageDao messageDao) {
        this.redisTemplate = redisTemplate;
        this.messageDao = messageDao;
    }

    @Override
    public Account findByUsername(String username) {
        Map<String, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();

        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                map.putAll(sc.hGetAll(accountHashKey(username)));
                set.addAll(sc.sMembers(accountAuthorityKey(username)));
                return null;
            }
        });

        return Account.fromRedisMapAndAuthorityMap(map, set);
    }

    @Override
    public RegisterResult register(Account account) {
        String username = account.getUsername();
        MutableBoolean isAccountExist = new MutableBoolean(false);
        Map<String, String> map = account.buildRedisMap();
        String[] authorities = account.buildRedisAuthoritiesArray();

        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                if (sc.sIsMember(ACCOUNT_USERNAMES, username)) {
                    isAccountExist.setValue(true);
                    return null;
                }
                sc.sAdd(ACCOUNT_USERNAMES, username);
                sc.hMSet(accountHashKey(username), map);
                sc.sAdd(accountAuthorityKey(username), authorities);
                return null;
            }
        });

        if (isAccountExist.isTrue()) {
            return RegisterResult.USERNAME_EXISTS;
        }

        return RegisterResult.OK;
    }

    /**
     * Messages have links to accounts, so this method deletes account with all his messages
     * */
    @Override
    public void delete(String username) {
        redisTemplate.opsForSet().members(messagesInAccountSetKey(username)).stream()
                .map(StringTypeConverter::toInteger).forEach(messageDao::delete);

        redisTemplate.delete(accountAuthorityKey(username));
        redisTemplate.delete(accountHashKey(username));
        redisTemplate.opsForSet().remove(ACCOUNT_USERNAMES, username);
    }

}
