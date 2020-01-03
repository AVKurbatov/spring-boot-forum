package ru.avkurbatov_home.dao.abstracts;

import org.junit.Test;
import ru.avkurbatov_home.dao.abstracts.AccountDao;
import ru.avkurbatov_home.enums.RegisterResult;
import ru.avkurbatov_home.jdo.Account;
import ru.avkurbatov_home.utils.TestUtils;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static ru.avkurbatov_home.utils.TestConstants.*;

public abstract class AccountDaoTest {

    @Inject
    private AccountDao accountDao;

    @Test
    public void shouldRegisterWithSeveralAuthorities(){

        // Given
        RegisterResult result = accountDao.register(TestUtils.createAccount());

        // Then
        Account foundAccount = accountDao.findByUsername(USERNAME);

        // When
        assertEquals(RegisterResult.OK, result);

        assertEquals(USERNAME, foundAccount.getUsername());
        assertEquals(PASSWORD, foundAccount.getPassword());
        assertEquals(EMAIL, foundAccount.getEmail());
        assertEquals(SEX, foundAccount.getSex());
        assertEquals(BIRTH_DATE, foundAccount.getBirthDate());
        assertEquals(AUTHORITIES, foundAccount.getAuthorities());
    }

    @Test
    public void shouldNotRegisterAccountWithExistingUsername(){

        // Given
        Account account = TestUtils.createAccount();
        RegisterResult firstResult = accountDao.register(account);

        // Then
        RegisterResult secondResult = accountDao.register(account);

        // When
        assertEquals(RegisterResult.OK, firstResult);
        assertEquals(RegisterResult.USERNAME_EXISTS, secondResult);
    }

}
