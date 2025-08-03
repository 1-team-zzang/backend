package com.example.calpick.domain.dto.response.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationsListResponseDto {
    public int page;
    public int totalPages;
    public List<NotificationDto> notifications;

    public static NotificationsListResponseDto toResponseDto(int page , int totalPages, List<NotificationDto> notifications){
        return new NotificationsListResponseDto(page,totalPages,notifications);
    }
}
