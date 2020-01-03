package ru.avkurbatov_home.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.avkurbatov_home.jdo.Account;
import ru.avkurbatov_home.jdo.Topic;

import static ru.avkurbatov_home.utils.TestConstants.*;

public final class TestUtils {

    private TestUtils() {}

    private static final String CREATE_DB = Utils.readScript("sql/create_db.sql");

    public static void createAndFlushJdbcDb(JdbcTemplate jdbcTemplate){
        jdbcTemplate.update(CREATE_DB);
    }

    public static Account createAccount() {
        Account registeredAccount = new Account();
        registeredAccount.setUsername(USERNAME);
        registeredAccount.setPassword(PASSWORD);
        registeredAccount.setEmail(EMAIL);
        registeredAccount.setSex(SEX);
        registeredAccount.setBirthDate(BIRTH_DATE);
        registeredAccount.setAuthorities(AUTHORITIES);

        return registeredAccount;
    }

    public static Topic createTopic() {
        Topic topic = new Topic();
        topic.setTitle(TOPIC_TITLE);

        return topic;
    }
}
