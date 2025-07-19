package com.example.calpick.domain.controller;

import com.example.calpick.domain.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 계정 관리 API")
public class UserController {
    private final UserService userService;

}
