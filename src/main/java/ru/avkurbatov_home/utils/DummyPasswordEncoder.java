package ru.avkurbatov_home.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

public class DummyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.matches(encode(rawPassword));
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
