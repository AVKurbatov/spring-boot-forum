package ru.avkurbatov_home.dao.abstracts;

import org.junit.Test;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.jdo.Topic;
import ru.avkurbatov_home.utils.TestUtils;

import javax.inject.Inject;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.avkurbatov_home.utils.TestConstants.TOPIC_TITLE;

public abstract class TopicDaoTest {

    @Inject
    private TopicDao topicDao;

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
}
