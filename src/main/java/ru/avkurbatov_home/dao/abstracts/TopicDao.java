package ru.avkurbatov_home.dao.abstracts;

import ru.avkurbatov_home.jdo.Topic;

import java.util.List;

/**
 * Dao for work with topic structure
 * */
public interface TopicDao {

    List<Topic> findAll();

    Topic findById(int id);

    Topic save(Topic topic);

}
