package ru.avkurbatov_home.dao.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.jdo.Topic;
import ru.avkurbatov_home.utils.Utils;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Profile("h2")
public class TopicDaoJdbc implements TopicDao {

    private static final String FIND_ALL_TOPICS_QUERY = Utils.readScript("sql/find_all_topics.sql");
    private static final String FIND_TOPIC_BY_ID_QUERY = Utils.readScript("sql/find_topic_by_id.sql");
    private static final String DELETE_TOPIC_BY_ID_QUERY = Utils.readScript("sql/delete_topic_by_id.sql");

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert topicInserter;


    @Inject
    public TopicDaoJdbc(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.topicInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("topics")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public List<Topic> findAll() {
        return jdbcTemplate.query(FIND_ALL_TOPICS_QUERY, this::mapRowToTopic);
    }

    @Override
    public Topic findById(int id) {
        try {
            return jdbcTemplate.queryForObject(FIND_TOPIC_BY_ID_QUERY, this::mapRowToTopic, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Topic save(Topic topic) {
        int id = topicInserter.executeAndReturnKey(topic.buildSqlMap()).intValue();
        topic.setId(id);
        return topic;
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_TOPIC_BY_ID_QUERY, id);
    }

    private Topic mapRowToTopic(ResultSet rs, int rowNum) throws SQLException {
        Topic topic = new Topic();
        topic.setId(rs.getInt("id"));
        topic.setTitle(rs.getString("title"));
        return topic;
    }
}
