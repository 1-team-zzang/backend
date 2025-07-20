package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.dto.user.UserDto;
import com.example.calpick.domain.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/user")
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 계정 관리 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public Response<UserDto> checkProfile(@AuthenticationPrincipal CustomUserDetails userDetails){
        return Response.success(userService.profile(userDetails));
    }
}
