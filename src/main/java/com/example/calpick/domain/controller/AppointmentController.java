package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.request.appointment.AppointmentAcceptRequestDto;
import com.example.calpick.domain.dto.request.appointment.AppointmentRequestDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestDetailResponseDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestListResponseDto;
import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.AppointmentService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment", description = "Appointment API")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/requests")
    public Response<Object> requestAppointment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody AppointmentRequestDto dto) throws Exception {
        appointmentService.requestAppointments(userDetails,dto);
        return Response.success();
    }

    @GetMapping("/requests")
    public Response<AppointmentRequestListResponseDto> getAppointmentRequestsList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                                                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                                                                  @RequestParam(name = "status") String status){
        return Response.success(appointmentService.getAppointmentRequestsList(userDetails.getEmail(),page,size,status));
    }

    @GetMapping("/requests/{appointmentId}")
    public Response<AppointmentRequestDetailResponseDto> getAppointmentRequests(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                                @PathVariable("appointmentId")Long appointmentId){

        return Response.success(appointmentService.getAppointmentRequest(userDetails.getEmail(),appointmentId));
    }

    @PutMapping("/requests")
    public Response<Object> acceptAppointmentRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @RequestBody AppointmentAcceptRequestDto dto) throws Exception {
        appointmentService.acceptAppointmentRequest(userDetails.getEmail(),dto.id,dto.content,dto.status);
        return Response.success();
    }
}
