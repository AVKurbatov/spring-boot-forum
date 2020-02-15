package ru.avkurbatov_home.dao.abstracts;

import org.junit.Test;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.jdo.Message;
import ru.avkurbatov_home.jdo.Topic;
import ru.avkurbatov_home.utils.TestUtils;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static ru.avkurbatov_home.utils.TestConstants.TOPIC_TITLE;
import static ru.avkurbatov_home.utils.TestConstants.USERNAME;

public abstract class TopicDaoTest {

    @Inject
    private TopicDao topicDao;
    @Inject
    private MessageDao messageDao;
    @Inject
    private AccountDao accountDao;

    @Test
    public void shouldSaveTopicAndSetId(){
        // Given
        Topic topic = TestUtils.createTopic();

        // Then
        topic = topicDao.save(topic);
        List<Topic> foundTopics = topicDao.findAll();

        // When
        assertNotNull(topic.getId());
        assertEquals(TOPIC_TITLE, topic.getTitle());

        assertEquals(1, foundTopics.size());
        assertEquals(topic, foundTopics.get(0));

        assertEquals(topic, topicDao.findById(topic.getId()));
    }

    @Test
    public void shouldRemoveMessagesThenDeleteTopic() {
        // Given
        Message message = new Message();

        Topic topic = topicDao.save(TestUtils.createTopic());
        message.setTopicId(topic.getId());

        accountDao.register(TestUtils.createAccount());
        message.setAccountUsername(USERNAME);

        message.setText("any text");

        messageDao.save(message);

        // When
        topicDao.delete(topic.getId());

        // Then
        assertNull(topicDao.findById(topic.getId()));
        assertEquals(0, messageDao.findTotalNumberOfPages(topic.getId()));

    }
}
