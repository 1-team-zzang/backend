package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.response.notification.NotificationDto;
import com.example.calpick.domain.dto.response.notification.NotificationProjection;
import com.example.calpick.domain.dto.response.notification.NotificationsListResponseDto;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.NotificationEvent;
import com.example.calpick.domain.entity.enums.NotificationType;
import com.example.calpick.domain.repository.NotificationRepository;
import com.example.calpick.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationsListResponseDto getNotificationsList(int page, int size,String email) {
        User user = userRepository.findByEmail(email).get();
        Pageable pageable = PageRequest.of(page-1, size);
        Page<NotificationProjection> pages = notificationRepository.findRelevantNotifications(user.getUserId(),pageable);

        List<NotificationDto> dtoList = pages.getContent().stream()
                .map(projection -> {
                    String content;

                    if (projection.getEvent().equals(NotificationEvent.ACCEPT.name())) {
                        boolean isRequester = projection.getRequesterId().equals(user.getUserId());
                        String name = isRequester ? projection.getReceiverName() : projection.getRequesterName();

                        String prefix;
                        if (projection.getType().equals(NotificationType.APPOINTMENT.name())) {
                            prefix = "님과의 ";
                        } else if (projection.getType().equals(NotificationType.FRIEND.name())) {
                            prefix = "님과 ";
                        } else {
                            prefix = "님과 "; // 기본값 혹은 예외 처리
                        }

                        content = name + prefix + projection.getContent();
                    } else {
                        content = projection.getContent();
                    }

                    NotificationDto dto = new NotificationDto();
                    dto.setContent(content);
                    dto.setCreatedAt(projection.getCreatedAt());
                    dto.setType(projection.getType());
                    return dto;
                })
                .collect(Collectors.toList());
        return NotificationsListResponseDto.toResponseDto(page,pages.getTotalPages(),dtoList);

    }

}
