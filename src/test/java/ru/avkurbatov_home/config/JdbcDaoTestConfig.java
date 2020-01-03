package ru.avkurbatov_home.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.avkurbatov_home.dao.abstracts.AccountDao;
import ru.avkurbatov_home.dao.abstracts.MessageDao;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.dao.jdbc.AccountDaoJdbc;
import ru.avkurbatov_home.dao.jdbc.MessageDaoJdbc;
import ru.avkurbatov_home.dao.jdbc.TopicDaoJdbc;

import javax.sql.DataSource;

@Configuration
@PropertySources({@PropertySource("classpath:application.properties")})
public class JdbcDaoTestConfig {

    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setSeparator(";")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public AccountDao accountDao() {
        return new AccountDaoJdbc(jdbcTemplate());
    }

    @Bean
    public TopicDao topicDao() {
        return new TopicDaoJdbc(jdbcTemplate());
    }

    @Bean
    public MessageDao messageDao(@Value("${message.dao.page.size}") int pageSize) {
        return new MessageDaoJdbc(pageSize, jdbcTemplate());
    }
}
