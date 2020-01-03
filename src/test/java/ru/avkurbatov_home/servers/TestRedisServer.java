package ru.avkurbatov_home.servers;

import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class TestRedisServer extends RedisServer {

    public TestRedisServer(int port) {
        super(port);
    }

    @PostConstruct
    public void postConstruct() {
        start();
    }

    @PreDestroy
    public void preDestroy() {
        stop();
    }
}
