package com.example.calpick.domain.dto.user;

import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user){
        this.user = user;
    }
    public Long getUserId() {
        return user.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    public String getEmail(){
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }
}
