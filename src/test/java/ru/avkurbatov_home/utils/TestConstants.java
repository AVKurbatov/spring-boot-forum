package ru.avkurbatov_home.utils;

import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.GrantedAuthority;
import ru.avkurbatov_home.enums.Authority;
import ru.avkurbatov_home.enums.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public interface TestConstants {
    // region Account parameters
    String USERNAME = "some_username";
    String PASSWORD = "some_password";
    String EMAIL = "some_email";
    Sex SEX = Sex.MALE;
    LocalDate BIRTH_DATE = LocalDate.of(1992, 1, 23);
    Set<Authority> AUTHORITIES = ImmutableSet.of(Authority.ADMIN, Authority.USER);
    // endregion

    // region Message parameters
    String ACCOUNT_USERNAME = "some_username";
    String TEXT = "message_text";
    LocalDateTime DATE = LocalDateTime.of(1992, 1, 23, 1,2, 3);
    // endregion

    // region Topic parameters
    String TOPIC_TITLE = "some_title";
    // endregion

}
