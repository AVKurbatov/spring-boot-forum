package ru.avkurbatov_home.dao.abstracts;

import ru.avkurbatov_home.jdo.Message;

import java.util.List;

/**
 * Dao for work with message structure
 * */
public interface MessageDao {

    List<Message> findForPage(int topicId, int pageNumber);

    int findTotalNumberOfPages(int topicId);

    Message save(Message message);

    void delete(int id);

}
