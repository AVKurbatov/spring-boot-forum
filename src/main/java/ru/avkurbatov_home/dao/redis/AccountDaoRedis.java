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
import ru.avkurbatov_home.enums.RegisterResult;
import ru.avkurbatov_home.jdo.Account;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.inject.Inject;
import java.util.*;

/**
 * Structure in redis:
 * ACCOUNT_USERNAMES --- set with all available usernames
 * ACCOUNT_PREFIX + ":username" --- hash with id and title
 * ACCOUNT_AUTHORITIES + ":username" --- set with authorities
 * */
// todo: messages have links to accounts, so method with delete account should delete all his messages
@Slf4j
@Repository
@Profile("redis")
public class AccountDaoRedis extends AbstractAccountDao {

    private static final String ACCOUNT_USERNAMES = "accounts";
    private static final String ACCOUNT_PREFIX = "account:";
    private static final String ACCOUNT_AUTHORITIES = "authorities:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Inject
    public AccountDaoRedis(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Account findByUsername(String username) {
        Map<String, String> map = new HashMap<>();
        Set<String> set = new HashSet<>();

        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                StringRedisConnection sc = new DefaultStringRedisConnection(connection);
                map.putAll(sc.hGetAll(hashKey(username)));
                set.addAll(sc.sMembers(authorityKey(username)));
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
                sc.hMSet(hashKey(username), map);
                sc.sAdd(authorityKey(username), authorities);
                return null;
            }
        });

        if (isAccountExist.isTrue()) {
            return RegisterResult.USERNAME_EXISTS;
        }

        return RegisterResult.OK;
    }

    private String hashKey(String username){
        return ACCOUNT_PREFIX + username;
    }

    private String authorityKey(String username){
        return ACCOUNT_AUTHORITIES + username;
    }
}
