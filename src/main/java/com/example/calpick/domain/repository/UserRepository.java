package com.example.calpick.domain.repository;

import com.example.calpick.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = " select user_id, email, password, name, profile_url, user_status, login_types, created_at, modified_at, deleted_at, share_token, id_token " +
                    " from users where email=:email",
            nativeQuery = true
    )
    Optional<User> findByEmail(@Param("email") String email);

    @Query(
            value = "select exists( select 1 from users where email=:email limit 1 )",
            nativeQuery = true
    )
    int existsByEmail(@Param("email") String email);

    @Query(
            value = " select user_id, email, password, name, profile_url, user_status, login_types, created_at, modified_at, deleted_at, share_token, id_token " +
                    " from users where id_token=:idToken",
            nativeQuery = true
    )
    Optional<User> findByUid(@Param("idToken") String idToken);
}
