package ru.avkurbatov_home.dao.jdbc;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.avkurbatov_home.config.JdbcDaoTestConfig;
import ru.avkurbatov_home.dao.abstracts.MessageDaoTest;
import ru.avkurbatov_home.utils.TestUtils;

import javax.inject.Inject;

@Import(JdbcDaoTestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MessageDaoJdbcTest extends MessageDaoTest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Before
    public void init(){
        TestUtils.createAndFlushJdbcDb(jdbcTemplate);
        generateTopicAndAccount();
    }
}
