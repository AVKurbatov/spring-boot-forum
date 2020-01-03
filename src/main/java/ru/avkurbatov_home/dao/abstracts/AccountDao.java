package ru.avkurbatov_home.dao.abstracts;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.avkurbatov_home.enums.RegisterResult;
import ru.avkurbatov_home.jdo.Account;

/**
 * Dao for work with account structure
 * */
public interface AccountDao extends UserDetailsService {

    Account findByUsername(String username);

    RegisterResult register(Account account);

}
