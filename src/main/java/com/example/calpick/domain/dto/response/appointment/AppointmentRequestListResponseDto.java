package com.example.calpick.domain.dto.response.appointment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentRequestListResponseDto {
    public int page;
    public int totalPages;
    public List<AppointmentRequestsDto> appointmentRequests;

    public static AppointmentRequestListResponseDto toResponseDto(int page ,int totalPages,List<AppointmentRequestsDto> appointmentRequests){
        return new AppointmentRequestListResponseDto(page,totalPages,appointmentRequests);
    }

}
