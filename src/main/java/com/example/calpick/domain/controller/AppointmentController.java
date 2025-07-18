package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.request.appointment.AppointmentAcceptRequestDto;
import com.example.calpick.domain.dto.request.appointment.AppointmentRequestDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestDetailResponseDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestListResponseDto;
import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/requests")
    public Response<Object> requestAppointment(@RequestBody AppointmentRequestDto dto) throws Exception { //약속 신청
        appointmentService.requestAppointments(dto);
        return Response.success();
    }

    @GetMapping("/requests")
    public Response<AppointmentRequestListResponseDto> getAppointmentRequestsList(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                                                                  @RequestParam(name = "status") String status){ //약속 신청 목록 조회
        return Response.success(appointmentService.getAppointmentRequestsList(page,size,status));
    }

    @GetMapping("/requests/{appointmentId}")
    public Response<AppointmentRequestDetailResponseDto> getAppointmentRequests(@PathVariable("appointmentId")Long appointmentId){ //약속 세부 내용 조회
        return Response.success(appointmentService.getAppointmentRequest(appointmentId));
    }

    @PutMapping("/requests")
    public Response<Object> acceptAppointmentRequest(@RequestBody AppointmentAcceptRequestDto dto) throws Exception { //약속 수락
        appointmentService.acceptAppointmentRequest(dto.id,dto.content,dto.status);
        return Response.success();
        
    }
}
