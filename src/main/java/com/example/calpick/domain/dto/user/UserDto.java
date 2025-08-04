package com.example.calpick.domain.dto.user;

import com.example.calpick.domain.entity.enums.LoginType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String email;
    private String name;
    private LoginType loginType;
    private String profileUrl;

    public UserDto(Long userId, String name, String email, LoginType loginType, String profileUrl){
        this.userId=userId;
        this.name=name;
        this.email=email;
        this.loginType=loginType;
        this.profileUrl=profileUrl;
    }
}
