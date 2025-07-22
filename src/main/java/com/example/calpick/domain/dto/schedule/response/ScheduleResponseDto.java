package com.example.calpick.domain.dto.schedule.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDto {
    public Long scheduleId;
    public String title;
    public String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public LocalDateTime endAt;

    public Boolean isRepeated;
    public String repeatRule;
    public Boolean isVisible;
    public LocalDateTime createdAt;
    public LocalDateTime modifiedAt;
    public Boolean isAllDay;
    public String repeatType;
    public Long repeatCount;
    public LocalDateTime repeatEndAt;
    public String color;
}
