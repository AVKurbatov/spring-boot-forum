package ru.avkurbatov_home.dao.redis;

final class StructureNames {

    static final String ACCOUNT_USERNAMES = "accounts";
    static final String ACCOUNT_PREFIX = "account:";
    static final String ACCOUNT_AUTHORITIES = "authorities:";

    static final String TOPIC_IDS_COUNTER = "topicIdCount";
    static final String TOPIC_IDS = "topicIds";
    static final String TOPIC_HASH_PREFIX = "topic:";

    static final String MESSAGE_IDS_COUNTER = "messageIdsCount";
    static final String MESSAGE_IDS = "messages";
    static final String MESSAGE_HASH_PREFIX = "message:";
    static final String TOPIC_IDS_WITH_MESSAGES_SET = "topicsWithMessages";
    static final String MESSAGE_IDS_IN_TOPIC_PREFIX = "messagesInTopic:";
    static final String ACCOUNT_IDS_WITH_MESSAGES_SET = "accountsWithMessages";
    static final String MESSAGE_IDS_IN_ACCOUNT_PREFIX = "messagesInAccount:";

    private StructureNames() {}

    static String accountHashKey(String username){
        return ACCOUNT_PREFIX + username;
    }

    static String accountAuthorityKey(String username){
        return ACCOUNT_AUTHORITIES + username;
    }

    static String topicHashKey(int id){
        return TOPIC_HASH_PREFIX + id;
    }

    static String messageHashKey(int id) {
        return MESSAGE_HASH_PREFIX + id;
    }

    static String messagesInTopicSetKey(int topicId) {
        return MESSAGE_IDS_IN_TOPIC_PREFIX + topicId;
    }

    static String messagesInAccountSetKey(String username){
        return MESSAGE_IDS_IN_ACCOUNT_PREFIX + username;
    }
}
