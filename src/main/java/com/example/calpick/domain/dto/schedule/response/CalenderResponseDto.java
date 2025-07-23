package com.example.calpick.domain.dto.schedule.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalenderResponseDto {
    public boolean owner;
    public List<ScheduleResponseDto> scheduleResponseList;
}
