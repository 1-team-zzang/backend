package com.example.calpick.domain.service.impl;

import com.example.calpick.domain.dto.schedule.request.ScheduleRequestDto;
import com.example.calpick.domain.dto.schedule.response.ScheduleResponseDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.entity.Schedule;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.repository.ScheduleRepository;
import com.example.calpick.domain.repository.UserRepository;
import com.example.calpick.domain.service.ScheduleService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public ScheduleResponseDto createSchedule(CustomUserDetails userDetails, ScheduleRequestDto request) {
        User user= userRepository.findByEmail(userDetails.getEmail()).orElseThrow(() -> new CalPickException(ErrorCode.INVALID_EMAIL));
        Schedule newSchedule = scheduleRepository.save(Schedule.of(request, user));
        return mapper.map(newSchedule, ScheduleResponseDto.class);
    }
}
