package ru.avkurbatov_home.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import ru.avkurbatov_home.dao.abstracts.AccountDao;
import ru.avkurbatov_home.enums.Authority;
import ru.avkurbatov_home.utils.DummyPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AccountDao accountDao;

    public SecurityConfig (AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new DummyPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(accountDao)
                .passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/", "/**")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/**")
            .hasRole(Authority.USER.id)
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/topics", true)
            .and()
            .logout()
            .logoutSuccessUrl("/")
            .and()
            .csrf()
            .ignoringAntMatchers("/h2-console/**")
            .and()
            .headers()
            .frameOptions()
            .sameOrigin();
    }

    @Bean
    public SpringSecurityDialect securityDialect() {
        return new SpringSecurityDialect();
    }


}

