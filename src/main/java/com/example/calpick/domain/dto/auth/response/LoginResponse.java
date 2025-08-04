package com.example.calpick.domain.dto.auth.response;

import com.example.calpick.domain.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    public String accessToken;
    public String refreshToken;
    public UserDto userDto;
}
