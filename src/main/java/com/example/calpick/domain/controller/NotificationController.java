package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.response.notification.NotificationDto;
import com.example.calpick.domain.dto.response.notification.NotificationsListResponseDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@Tag(name = "Notification", description = "Notification API")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Response<NotificationsListResponseDto> getNotificationsList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @RequestParam(name = "page", defaultValue = "1")int page,
                                                                       @RequestParam(name = "size", defaultValue = "10")int size){
        return Response.success(notificationService.getNotificationsList(page,size,userDetails.getEmail()));

    }
}
