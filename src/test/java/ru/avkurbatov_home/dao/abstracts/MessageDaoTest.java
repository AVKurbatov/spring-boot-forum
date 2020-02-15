package ru.avkurbatov_home.dao.abstracts;

import org.junit.Test;
import ru.avkurbatov_home.dao.abstracts.AccountDao;
import ru.avkurbatov_home.dao.abstracts.MessageDao;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.jdo.Message;
import ru.avkurbatov_home.jdo.Topic;
import ru.avkurbatov_home.utils.TestUtils;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.avkurbatov_home.utils.TestConstants.*;

public abstract class MessageDaoTest {

    private int topicId;

    @Inject
    private TopicDao topicDao;
    @Inject
    private AccountDao accountDao;
    @Inject
    private MessageDao messageDao;

    protected void generateTopicAndAccount() {
        Topic topic = TestUtils.createTopic();
        topic = topicDao.save(topic);
        topicId = topic.getId();

        accountDao.register(TestUtils.createAccount());
    }

    @Test
    public void shouldSaveAndFindDate() {
        // Given
        Message message = createMessage();

        // When
        messageDao.save(message);

        // Then
        List<Message> messageList = messageDao.findForPage(topicId, 0);
        Message foundMessage = messageList.get(0);
        assertNotNull(foundMessage.getId());
        assertEquals(topicId, message.getId().intValue());
        assertEquals(ACCOUNT_USERNAME, message.getAccountUsername());
        assertEquals(TEXT, message.getText());
        assertEquals(DATE, foundMessage.getDate());
    }

    @Test
    public void shouldHandleEmptyPage() {
        // For empty table
        assertEquals(0, messageDao.findTotalNumberOfPages(topicId));

        // filling in the table
        Message message = createMessage();
        messageDao.save(message);

        // For not empty table
        assertEquals(1, messageDao.findTotalNumberOfPages(topicId));
        assertEquals(1, messageDao.findForPage(topicId,0).size());
        assertEquals(0, messageDao.findForPage(topicId,1).size());
    }

    private Message createMessage() {
        Message message = new Message();
        message.setTopicId(topicId);
        message.setAccountUsername(ACCOUNT_USERNAME);
        message.setDate(DATE);
        message.setText(TEXT);

        return message;
    }


}

