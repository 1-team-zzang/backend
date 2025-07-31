package com.example.calpick.domain.dto.user;

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
    private String profileUrl;

    public UserDto(Long userId, String name, String email, String profileUrl){
        this.userId=userId;
        this.name=name;
        this.email=email;
        this.profileUrl=profileUrl;
    }
}
