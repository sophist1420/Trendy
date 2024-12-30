package com.trendy.login;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId(); // User 엔티티의 userId 반환
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // User 엔티티의 username 반환
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // User 엔티티의 password 반환
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 설정 (기본적으로 ROLE_USER를 부여)
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음을 반환
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았음을 반환
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않았음을 반환
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되었음을 반환
    }
}
