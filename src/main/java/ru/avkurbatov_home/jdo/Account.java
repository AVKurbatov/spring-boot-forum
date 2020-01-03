package ru.avkurbatov_home.jdo;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.avkurbatov_home.enums.Authority;
import ru.avkurbatov_home.enums.Sex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User data.
 * */
@Data
public class Account implements UserDetails {

    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    private String username;
    private String password;
    private String email;
    private Sex sex;
    private LocalDate birthDate;

    private Set<Authority> authorities = new HashSet<>();

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void addAuthority(Authority authority){
        authorities.add(authority);
    }

    public boolean hasAuthority(String authority){
        return authorities.contains(Authority.of(authority));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Map<String, String> buildRedisMap(){
        return ImmutableMap.<String, String>builder()
                .put("username", username)
                .put("password", password)
                .put("email", email)
                .put("sex", sex.db)
                .put("birthDate", birthDate.format(formatter))
                .build();
    }

    public String[] buildRedisAuthoritiesArray(){
        return authorities.stream().map(authority -> authority.id).toArray(size -> new String[authorities.size()]);
    }

    public static Account fromRedisMapAndAuthorityMap(Map<String, String> map, Set<String> authorities){
        if ( (map == null || map.isEmpty()) && (authorities == null || authorities.isEmpty()) ) {
            return null;
        }
        Account account = new Account();
        if (map != null) {
            account.setUsername(map.get("username"));
            account.setPassword(map.get("password"));
            account.setEmail(map.get("email"));
            account.setSex(Sex.of(map.get("sex")));
            account.setBirthDate(LocalDate.parse(map.get("birthDate"), formatter));
        }
        if (authorities != null){
            account.setAuthorities(authorities.stream().map(Authority::of).collect(Collectors.toSet()));
        }

        return account;
    }

}

