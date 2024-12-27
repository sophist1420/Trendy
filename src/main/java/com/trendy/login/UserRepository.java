package com.trendy.login;

import org.springframework.data.jpa.repository.JpaRepository;

// User 엔티티를 관리하는 JPA Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 검색
    User findByEmail(String email);
}
