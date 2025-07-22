package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleResponseDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;

public interface ScheduleService {
    ScheduleResponseDto createSchedule(CustomUserDetails userDetails, ScheduleRequestDto request);
    ScheduleResponseDto getSchedule(CustomUserDetails userDetails, Long scheduleId);
}
