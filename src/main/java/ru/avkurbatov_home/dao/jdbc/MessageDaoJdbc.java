package ru.avkurbatov_home.dao.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.avkurbatov_home.dao.abstracts.MessageDao;
import ru.avkurbatov_home.jdo.Message;
import ru.avkurbatov_home.utils.DateTypeConverter;
import ru.avkurbatov_home.utils.Utils;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@Profile("h2")
public class MessageDaoJdbc implements MessageDao {

    private static final String FIND_MESSAGES_FOR_PAGE_QUERY =
            Utils.readScript("sql/find_messages_for_page.sql");
    private static final String COUNT_MESSAGES_QUERY =
            Utils.readScript("sql/count_messages.sql");
    private static final String DELETE_MESSAGE_BY_ID_QUERY =
            Utils.readScript("sql/delete_message_by_id.sql");

    private final int pageSize;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert messageInserter;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    public MessageDaoJdbc(@Value("${message.dao.page.size}") int pageSize,
                          JdbcTemplate jdbcTemplate){
        this.pageSize = pageSize;
        this.jdbcTemplate = jdbcTemplate;
        this.messageInserter = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("messages")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public List<Message> findForPage(int topicId, int pageNumber) {
        return jdbcTemplate.query(FIND_MESSAGES_FOR_PAGE_QUERY, (rs, num) -> this.mapRowToMessage(rs), topicId,
                pageNumber * pageSize, (pageNumber + 1) * pageSize);
    }

    @Override
    public int findTotalNumberOfPages(int topicId) {
        Integer numberOfMessages = jdbcTemplate.queryForObject(COUNT_MESSAGES_QUERY, Integer.class, topicId);
        if (numberOfMessages == null) {
            throw new IllegalArgumentException("TopicId " + topicId + " is absent in database");
        }
        return (int)Math.ceil((double)numberOfMessages / pageSize);
    }

    @Override
    public Message save(Message message) {
        if (message.getDate() == null) {
            message.setDate(LocalDateTime.now());
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map =
                OBJECT_MAPPER.convertValue(message, Map.class);
        map.put("date", message.getDate());
        int id = messageInserter.executeAndReturnKey(map).intValue();
        message.setId(id);
        return message;

    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(DELETE_MESSAGE_BY_ID_QUERY, id);
    }

    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setTopicId(rs.getInt("topicId"));
        message.setAccountUsername(rs.getString("accountUsername"));
        message.setDate(DateTypeConverter.toLocalDateTime(rs.getTimestamp("date")));
        message.setText(rs.getString("text"));

        return message;
    }
}
