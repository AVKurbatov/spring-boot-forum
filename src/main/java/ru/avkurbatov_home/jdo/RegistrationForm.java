package ru.avkurbatov_home.jdo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.avkurbatov_home.enums.Authority;
import ru.avkurbatov_home.enums.Sex;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 *  RegistrationForm is used to create Account from form. Some default parameters are putted here.
 * */
@Data
public class RegistrationForm {

    @NotBlank
    @Size(max=30, message="Username must be no more 30 characters long")
    private String username;

    @NotBlank
    @Size(max=30, message="Password must be no more 30 characters long")
    private String password;

    @Email
    @NotBlank
    @Size(max=50, message="Email must be no more 50 characters long")
    private String email;

    @NotNull(message="You must be male or female")
    private Sex sex;

    @NotNull
    @Past(message="You must have some birth date from the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    public Account toAccount(PasswordEncoder passwordEncoder) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setSex(sex);
        account.setBirthDate(birthDate);
        account.addAuthority(Authority.USER);
        return account;
    }
}