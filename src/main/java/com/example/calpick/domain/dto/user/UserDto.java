package com.example.calpick.domain.dto.user;

import com.example.calpick.domain.entity.enums.LoginType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String email;
    private String name;
    private Set<LoginType> loginTypes;
    private String profileUrl;

    public UserDto(Long userId, String name, String email, Set<LoginType> loginTypes, String profileUrl){
        this.userId=userId;
        this.name=name;
        this.email=email;
        this.loginTypes=loginTypes;
        this.profileUrl=profileUrl;
    }
}
