package ru.avkurbatov_home.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

public final class Utils {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Utils() {}

    public static String readScript(String filename) {
        try {
            return IOUtils.toString(new ClassPathResource(filename).getInputStream());
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void flushRedisDb(RedisTemplate<String, Object> redisTemplate) {
        redisTemplate.execute(connection -> {
            connection.flushDb();
            return null;
        }, true);
    }

}
