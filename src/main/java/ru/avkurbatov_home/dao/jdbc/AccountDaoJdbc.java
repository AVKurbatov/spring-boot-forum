package ru.avkurbatov_home.dao.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.avkurbatov_home.dao.abstracts.AbstractAccountDao;
import ru.avkurbatov_home.enums.Authority;
import ru.avkurbatov_home.enums.RegisterResult;
import ru.avkurbatov_home.enums.Sex;
import ru.avkurbatov_home.jdo.Account;
import ru.avkurbatov_home.utils.DateTypeConverter;
import ru.avkurbatov_home.utils.Utils;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@Profile("h2")
public class AccountDaoJdbc extends AbstractAccountDao {

    private static final AccountExtractor ACCOUNT_EXTRACTOR = new AccountExtractor();
    private static final String FIND_USER_BY_USERNAME_QUERY =
            Utils.readScript("sql/find_user_by_username.sql");
    private static final String INSERT_BASIC_PARAMS_INTO_ACCOUNT =
            Utils.readScript("sql/insert_basic_params_into_account.sql");
    private static final String INSERT_AUTHORITIES_INTO_ACCOUNT =
            Utils.readScript("sql/insert_authorities_into_account.sql");
    private static final String DELETE_USER_BY_USERNAME_QUERY =
            Utils.readScript("sql/delete_user_by_username.sql");
    private static final String DELETE_AUTHORITIES_FOR_USERNAME_QUERY =
            Utils.readScript("sql/delete_authorities_for_username.sql");

    private final JdbcTemplate jdbcTemplate;

    @Inject
    public AccountDaoJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findByUsername(String username) {
        return jdbcTemplate.query(FIND_USER_BY_USERNAME_QUERY, ACCOUNT_EXTRACTOR, username);
    }

    @Override
    @Transactional
    public RegisterResult register(Account account) {
        try {
            jdbcTemplate.update(INSERT_BASIC_PARAMS_INTO_ACCOUNT,
                    account.getUsername(), account.getPassword(), account.getEmail(),
                    account.getSex().db, account.getBirthDate());
        } catch (DuplicateKeyException e){
            log.warn("Someone tried register account with existed username {}", account.getUsername());
            return RegisterResult.USERNAME_EXISTS;
        }

        List<? extends GrantedAuthority> authorityList = new ArrayList<>(account.getAuthorities());
        jdbcTemplate.batchUpdate(INSERT_AUTHORITIES_INTO_ACCOUNT, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setString(1, account.getUsername());
                ps.setString(2, authorityList.get(i).toString());
            }

            @Override
            public int getBatchSize() {
                return authorityList.size();
            }
        });

        return RegisterResult.OK;
    }

    @Override
    @Transactional
    public void delete(String username) {
        jdbcTemplate.update(DELETE_USER_BY_USERNAME_QUERY, username);
        jdbcTemplate.update(DELETE_AUTHORITIES_FOR_USERNAME_QUERY, username);
    }

    private static class AccountExtractor implements ResultSetExtractor<Account>{
        public Account extractData(ResultSet rs) throws SQLException {
            if (!rs.next()) {
                return null;
            }

            Account account = new Account();
            account.setUsername(rs.getString("username"));
            account.setPassword(rs.getString("password"));
            account.setEmail(rs.getString("email"));
            account.setSex(Sex.of(rs.getString("sex")));
            account.setBirthDate(DateTypeConverter.toLocalDate(rs.getDate("birthDate")));

            do {
                account.addAuthority(Authority.of(rs.getString("authority")));
            } while (rs.next());

            return account;
        }
    }

}
