package com.example.calpick.domain.dto.request.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentRejectRequestDto {
    public Long id;
    public String content;
}
