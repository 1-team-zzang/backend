package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleResponseDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    Response<ScheduleResponseDto> createSchedule(@AuthenticationPrincipal CustomUserDetails user, ScheduleRequestDto requestDto){
        return Response.success(scheduleService.createSchedule(user, requestDto));
    }

}
