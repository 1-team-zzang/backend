package com.example.calpick.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentRequestDto {
    public String title;
    public String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    public LocalDateTime endAt;
    public String requesterName; //비회원
    public String requesterEmail;  //비회원
    public Boolean isAllDay;
    public Long receiverId;


}
