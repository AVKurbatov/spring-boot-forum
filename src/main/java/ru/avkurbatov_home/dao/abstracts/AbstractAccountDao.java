package ru.avkurbatov_home.dao.abstracts;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.avkurbatov_home.jdo.Account;

/**
 * Implementation of UserDetailsService in AccountDaos with Adapter pattern.
 * */
public abstract class AbstractAccountDao implements AccountDao {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("For username " + username);
        }
        return account;
    }

}
