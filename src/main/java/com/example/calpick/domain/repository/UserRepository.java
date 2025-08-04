package com.example.calpick.domain.repository;

import com.example.calpick.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = " select user_id, email, password, name, profile_url, user_status, login_type, created_at, modified_at, deleted_at, share_token " +
                    " from users where login_type = 'NORMAL' and email=:email",
            nativeQuery = true
    )
    Optional<User> findByEmail(@Param("email") String email);

    @Query(
            value = "select exists( select 1 from users where  login_type=:loginType and email=:email limit 1 )",
            nativeQuery = true
    )
    int existsByEmail(@Param("email") String email,
                      @Param("loginType") String loginType);

    @Query(
            value = " select user_id, email, password, name, profile_url, user_status, login_type, created_at, modified_at, deleted_at, share_token " +
                    " from users where login_type='KAKAO' and password=:idToken",
            nativeQuery = true
    )
    Optional<User> findByUid(@Param("idToken") String idToken);
}
