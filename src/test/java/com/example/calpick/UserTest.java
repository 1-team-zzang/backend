package com.example.calpick;

import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.LoginType;
import com.example.calpick.domain.entity.enums.UserStatus;
import com.example.calpick.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class UserTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback(false)
    void 유저_저장_및_조회() {
        User user = User.builder()
                .email("llynn97@naver.com")
                .password("1111")
                .name("홍길동")
                .userStatus(UserStatus.ACTIVE)
                .loginType(LoginType.NORMAL)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        User savedUser = userRepository.save(user);


    }
}
