package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.dto.schedule.response.CalenderResponseDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleResponseDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleShareDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import java.time.LocalDate;

public interface ScheduleService {
    ScheduleResponseDto createSchedule(CustomUserDetails userDetails, ScheduleRequestDto request);
    ScheduleResponseDto getSchedule(CustomUserDetails userDetails, Long scheduleId);
    CalenderResponseDto getOwnCalendar(CustomUserDetails userDetails, LocalDate startDate, LocalDate endDate);
    CalenderResponseDto getOtherCalendar(CustomUserDetails userDetails, Long calendarUserId, LocalDate startDate, LocalDate endDate);
    ScheduleResponseDto updateSchedule(CustomUserDetails userDetails, Long scheduleId, ScheduleRequestDto request);
    void deleteSchedule(CustomUserDetails userDetails, Long scheduleId);
    ScheduleShareDto shareMyCalendar(CustomUserDetails userDetails);
}
