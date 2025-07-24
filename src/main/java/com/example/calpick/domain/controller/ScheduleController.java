package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.dto.schedule.response.CalenderResponseDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleResponseDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleShareDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedule", description = "Schedule API")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    Response<ScheduleResponseDto> createSchedule(@AuthenticationPrincipal CustomUserDetails user, @RequestBody ScheduleRequestDto requestDto){
        return Response.success(scheduleService.createSchedule(user, requestDto));
    }
    @GetMapping
    Response<CalenderResponseDto> getOwnCalendar(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestParam("start")LocalDate startDate, @RequestParam("end") LocalDate endDate){
        return Response.success(scheduleService.getOwnCalendar(userDetails, startDate, endDate));
    }

    @GetMapping("/user/{userId}")
    Response<CalenderResponseDto> getOthersCalendar(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @PathVariable("userId") Long userId,
                                                    @RequestParam("start")LocalDate startDate,
                                                    @RequestParam("end") LocalDate endDate){
        return Response.success(scheduleService.getOtherCalendar(userDetails, userId, startDate, endDate));
    }

    @GetMapping("/{scheduleId}")
    Response<ScheduleResponseDto> getSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable("scheduleId") Long scheduleId){
        return Response.success(scheduleService.getSchedule(userDetails, scheduleId));
    }

    @PutMapping("/{scheduleId}")
    Response<ScheduleResponseDto> editSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @PathVariable("scheduleId") Long scheduleId,
                                               @RequestBody ScheduleRequestDto requestDto){
        return Response.success(scheduleService.updateSchedule(userDetails, scheduleId, requestDto));
    }

    @DeleteMapping("/{scheduleId}")
    Response<Object> deleteSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable("scheduleId") Long scheduleId){
        scheduleService.deleteSchedule(userDetails, scheduleId);
        return Response.success();
    }

    @GetMapping("/share")
    Response<ScheduleShareDto> shareCalendar(@AuthenticationPrincipal CustomUserDetails userDetails){
        return Response.success(scheduleService.shareMyCalendar(userDetails));
    }
}
