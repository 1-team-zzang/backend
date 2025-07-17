package com.example.calpick.service;

import com.example.calpick.domain.dto.request.AppointmentRejectRequestDto;
import com.example.calpick.domain.dto.request.AppointmentRequestDto;
import com.example.calpick.domain.dto.response.AppointmentRequestDetailResponseDto;
import com.example.calpick.domain.dto.response.AppointmentRequestListResponseDto;
import com.example.calpick.domain.dto.response.AppointmentRequestsDto;
import com.example.calpick.domain.entity.*;
import com.example.calpick.domain.entity.enums.AppointmentStatus;
import com.example.calpick.domain.entity.enums.NotificationEvent;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import com.example.calpick.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final ScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final MailService mailService;

    private final ModelMapper modelMapper;
    @Transactional(rollbackFor = Exception.class)
    public void requestAppointments(AppointmentRequestDto dto) throws Exception {
        Long userId = 2L;
        User user = userRepository.findById(userId).get(); //회원일때
        User receiver = userRepository.findById(dto.getReceiverId()).get();

        if(dto.getIsAllDay()){ //종일 일때
            LocalDate date = dto.getStartAt().toLocalDate();
            dto.setEndAt(date.atTime(23, 59));
        }

        if(!dto.getStartAt().isBefore(dto.getEndAt())){ //종료시간이 시작시간 보다 먼저일때,
            throw new CalPickException(ErrorCode.INVALID_TIME_RANGE);
        }

        if(!scheduleRepository.findOverlappingSchedules(dto.startAt,dto.endAt,dto.receiverId).isEmpty()){
            throw new CalPickException(ErrorCode.DUPLICATE_APPOINTMENT_TIME);
        }

        if(dto.requesterEmail.isEmpty() && !scheduleRepository.findOverlappingSchedules(dto.startAt,dto.endAt,userId).isEmpty()){ //요청자가 회원일때 요청자 일정도 확인
            throw new CalPickException(ErrorCode.DUPLICATE_APPOINTMENT_TIME);
        }

        Appointment appointment = modelMapper.map(dto, Appointment.class);
        appointment.setRequester(null);
        appointment.setAppointmentId(null);
        appointment.setAppointmentStatus(AppointmentStatus.REQUESTED);
        if(dto.requesterEmail.isEmpty()){ //요청자가 회원일경우
            appointment.setRequester(user);
            appointment.setRequesterName(user.getName());
        }
        appointment.setReceiver(receiver);
        appointment.setCreatedAt(LocalDateTime.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);


        //수신자에게 신청 알람 메일 전송
        Notification notification = Notification.of(savedAppointment,NotificationEvent.REQUEST,dto.content);
        Notification savedNotification = notificationRepository.save(notification);


        NotificationType notificationType = NotificationType.of(com.example.calpick.domain.entity.enums.NotificationType.APPOINTMENT,savedAppointment.getAppointmentId(),savedNotification);
        notificationTypeRepository.save(notificationType);

        String message = dto.getRequesterName() + " 님이 약속을 신청하셨습니다. 약속 신청 목록을 확인해주세요.";

        mailService.sendSimpleMessageAsync(receiver.getEmail(),"캘픽 약속 신청",savedNotification.getNotificationId(),message);
    }

    @Transactional
    public AppointmentRequestListResponseDto getAppointmentRequestsList(int page, int size, String status){
        Long userId = 1L;
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<AppointmentStatus> statusList;

        if(status.equals("REQUESTED")){ //대기 중 약속 목록

            statusList = List.of(AppointmentStatus.REQUESTED);
        }else{ //응답한 약속 목록
            statusList = List.of(AppointmentStatus.ACCEPTED, AppointmentStatus.REJECTED);
        }
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        modelMapper.typeMap(Appointment.class, AppointmentRequestsDto.class)
                .addMapping(Appointment::getAppointmentId, AppointmentRequestsDto::setId)
                .addMapping(Appointment::getCreatedAt, AppointmentRequestsDto::setInviteAt)
                .addMapping(Appointment::getRequesterName, AppointmentRequestsDto::setRequesterName);

        Page<Appointment> appointments = appointmentRepository.findByReceiverIdAndStatuses(userId,statusList,pageable);

        List<AppointmentRequestsDto> dtoList = appointments
                .getContent()
                .stream()
                .map(appointment -> modelMapper.map(appointment,AppointmentRequestsDto.class))
                .collect(Collectors.toList());

        return AppointmentRequestListResponseDto.toResponseDto(page,appointments.getTotalPages(),dtoList);
    }

    @Transactional
    public AppointmentRequestDetailResponseDto getAppointmentRequest(Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new CalPickException(ErrorCode.APPOINTMENT_NOT_FOUND));
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.typeMap(Appointment.class, AppointmentRequestDetailResponseDto.class)
                .addMapping(Appointment::getAppointmentId, AppointmentRequestDetailResponseDto::setId)
                .addMapping(Appointment::getCreatedAt, AppointmentRequestDetailResponseDto::setInviteAt)
                .addMapping(Appointment::getRequesterName, AppointmentRequestDetailResponseDto::setRequesterName);
        return modelMapper.map(appointment, AppointmentRequestDetailResponseDto.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptAppointmentRequest(Long id,String content, String status) throws Exception {
        if(status.equals("ACCEPT")){
            acceptRequest(id);
        }else if(status.equals("REJECT")){
            rejectRequest(id,content);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptRequest(Long id) throws Exception {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new CalPickException(ErrorCode.APPOINTMENT_NOT_FOUND));
        appointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
        appointment.setModifiedAt(LocalDateTime.now());

        if(!scheduleRepository.findOverlappingSchedules(appointment.getStartAt(),appointment.getEndAt(),appointment.getReceiver().getUserId()).isEmpty()){
            throw new CalPickException(ErrorCode.DUPLICATE_APPOINTMENT_TIME);
        }

        if(appointment.getRequesterEmail().isEmpty() && !scheduleRepository.findOverlappingSchedules(appointment.getStartAt(),appointment.getEndAt(),appointment.getRequester().getUserId()).isEmpty()){ //요청자가 회원일때 요청자 일정도 확인
            throw new CalPickException(ErrorCode.DUPLICATE_APPOINTMENT_TIME);
        }

        //수락자 회원 일정 추가
        Schedule receiverSchedule = Schedule.of(appointment,appointment.getReceiver());
        scheduleRepository.save(receiverSchedule);

        if(appointment.getRequester()!=null){ //회원 요청자 일정 추가
            Schedule requesterSchedule = Schedule.of(appointment,appointment.getRequester());
            scheduleRepository.save(requesterSchedule);
        }

        Notification notification = Notification.of(appointment,NotificationEvent.ACCEPT,"약속이 확정되었습니다");
        notificationRepository.save(notification);


        NotificationType notificationType = NotificationType.of(com.example.calpick.domain.entity.enums.NotificationType.APPOINTMENT,appointment.getAppointmentId(),notification);
        notificationTypeRepository.save(notificationType);

        String message = appointment.getTitle()+" 약속이 확정되었습니다.";

        //수신자 수락 알림 메일 발송
        mailService.sendSimpleMessageAsync(appointment.getReceiver().getEmail(),"캘픽 약속 확정",notification.getNotificationId(),message);

        String requesterEmail = "";

        if(appointment.getRequester()!=null){
            requesterEmail = appointment.getRequester().getEmail();
        }else{
            requesterEmail = appointment.getRequesterEmail();
        }

        //요청자 알림 메일 발송
        mailService.sendSimpleMessageAsync(requesterEmail,"캘픽 약속 확정",notification.getNotificationId(),message);


    }


    @Transactional(rollbackFor = Exception.class)
    public void rejectRequest(Long id, String content) throws Exception {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new CalPickException(ErrorCode.APPOINTMENT_NOT_FOUND));

        appointment.setAppointmentStatus(AppointmentStatus.REJECTED);

        Notification notification = Notification.of(appointment,NotificationEvent.REJECT,content);
        notificationRepository.save(notification);

        NotificationType notificationType = NotificationType.of(com.example.calpick.domain.entity.enums.NotificationType.APPOINTMENT,appointment.getAppointmentId(),notification);
        notificationTypeRepository.save(notificationType);

        //요청자에게 거절 메일 발송
        String message = appointment.getReceiver().getName() + " 님으로부터 거절 메시지: "+ content;


        String requesterEmail = "";

        if(appointment.getRequester()!=null){
            requesterEmail = appointment.getRequester().getEmail();
        }else{
            requesterEmail = appointment.getRequesterEmail();
        }

        //요청자 알림 메일 발송
        mailService.sendSimpleMessageAsync(requesterEmail,"캘픽 약속 거절",notification.getNotificationId(),message);
    }
}
