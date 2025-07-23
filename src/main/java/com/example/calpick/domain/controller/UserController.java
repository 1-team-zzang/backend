package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.domain.dto.user.UserPasswordRequestDto;
import com.example.calpick.domain.dto.user.UserProfileRequestDto;
import com.example.calpick.domain.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 계정 관리 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public Response<UserDto> checkProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        return Response.success(userService.profile(userDetails));
    }

    @PutMapping("/profile")
    public Response<UserDto> editProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody UserProfileRequestDto request){
        return Response.success(userService.editProfile(userDetails, request));
    }

    @PutMapping("/password")
    public Response<Object> editPassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody UserPasswordRequestDto request){
        userService.editPassword(userDetails, request);
        return Response.success();
    }
}
