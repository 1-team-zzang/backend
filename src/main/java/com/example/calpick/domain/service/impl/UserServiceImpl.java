package com.example.calpick.domain.service.impl;

import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.domain.dto.user.UserProfileRequestDto;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.repository.UserRepository;
import com.example.calpick.domain.service.UserService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public UserDto profile(CustomUserDetails userDetails) {
        if (userDetails.getEmail() == null) throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(()->new CalPickException(ErrorCode.INVALID_EMAIL));
        return mapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto editProfile(CustomUserDetails userDetails, UserProfileRequestDto request) {
        if (userDetails.getEmail() == null) throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(()->new CalPickException(ErrorCode.INVALID_EMAIL));
        user.setName(request.getName());
        user.setProfileUrl(request.getProfileUrl());
        return mapper.map(user, UserDto.class);
    }
}
