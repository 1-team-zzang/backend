package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.dto.user.UserDto;

public interface UserService {
    UserDto profile(CustomUserDetails userDetails);
}
