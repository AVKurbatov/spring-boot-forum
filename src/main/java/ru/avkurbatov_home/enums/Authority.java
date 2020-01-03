package ru.avkurbatov_home.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER");

    public final String id;

    Authority(String id){
        this.id = id;
    }

    @Override
    public String toString(){
        return id;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + id;
    }

    public static Authority of(String id) {
        if (id == null) {
            return null;
        }
        for (Authority authority : values()) {
            if (authority.id.equals(id)) {
                return authority;
            }
        }
        throw new IllegalArgumentException("No Authority is defined for id-value " + id);
    }

}
