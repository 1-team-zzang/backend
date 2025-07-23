package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.domain.dto.user.UserPasswordRequestDto;
import com.example.calpick.domain.dto.user.UserProfileRequestDto;

public interface UserService {
    UserDto profile(CustomUserDetails userDetails);
    UserDto editProfile(CustomUserDetails userDetails, UserProfileRequestDto request);
    void editPassword(CustomUserDetails userDetails, UserPasswordRequestDto request);
}
