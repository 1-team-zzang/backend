package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.request.appointment.AppointmentRequestDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestDetailResponseDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestListResponseDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestsDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.entity.*;
import com.example.calpick.domain.entity.enums.AppointmentStatus;
import com.example.calpick.domain.entity.enums.NotificationEvent;
import com.example.calpick.domain.repository.*;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
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
import java.time.format.DateTimeFormatter;
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
    public void requestAppointments(CustomUserDetails userDetails, AppointmentRequestDto dto) throws Exception {
        User user = null;
        if(userDetails != null){ //회원
            user = userRepository.findByEmail(userDetails.getEmail()).get();
        }
        User receiver = userRepository.findById(dto.getReceiverId()).get();

        if(dto.getIsAllDay()){ //종일 일때
            LocalDate date = dto.getStartAt().toLocalDate();
            dto.setEndAt(date.atTime(23, 59));
        }

        if(!dto.getStartAt().isBefore(dto.getEndAt())){ //종료시간이 시작시간 보다 먼저일때,
            throw new CalPickException(ErrorCode.INVALID_TIME_RANGE);
        }

        if (dto.startAt.isBefore(LocalDateTime.now())) {
            throw new CalPickException(ErrorCode.INVALID_APPOINTMENT_TIME);
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String date = dto.getStartAt().format(formatter) + " ~ " + dto.getEndAt().format(formatter);

        mailService.sendSimpleMessageAsync(receiver.getEmail(),dto.requesterName, dto.getTitle(),savedNotification.getNotificationId(),date,"","REQUEST");
    }

    @Transactional
    public AppointmentRequestListResponseDto getAppointmentRequestsList(String email,int page, int size, String status){
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        User user = userRepository.findByEmail(email).get();
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
                .addMapping(Appointment::getRequesterName, AppointmentRequestsDto::setRequesterName)
                .addMapping(Appointment::getAppointmentStatus, AppointmentRequestsDto::setStatus);

        Page<Appointment> appointments = appointmentRepository.findByReceiverIdAndStatuses(user.getUserId(),statusList,pageable);

        List<AppointmentRequestsDto> dtoList = appointments
                .getContent()
                .stream()
                .map(appointment -> modelMapper.map(appointment,AppointmentRequestsDto.class))
                .collect(Collectors.toList());

        return AppointmentRequestListResponseDto.toResponseDto(page,appointments.getTotalPages(),dtoList);
    }

    @Transactional
    public AppointmentRequestDetailResponseDto getAppointmentRequest(String email,Long id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new CalPickException(ErrorCode.APPOINTMENT_NOT_FOUND));
        User user = userRepository.findByEmail(email).get();

        if(appointment.getReceiver().getUserId() != user.getUserId()){
            throw new CalPickException(ErrorCode.NO_ACCESS_TO_APPOINTMENT_REQUEST);
        }
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.typeMap(Appointment.class, AppointmentRequestDetailResponseDto.class)
                .addMapping(Appointment::getAppointmentId, AppointmentRequestDetailResponseDto::setId)
                .addMapping(Appointment::getCreatedAt, AppointmentRequestDetailResponseDto::setInviteAt)
                .addMapping(Appointment::getRequesterName, AppointmentRequestDetailResponseDto::setRequesterName)
                .addMapping(Appointment::getAppointmentStatus,AppointmentRequestDetailResponseDto::setStatus);
        return modelMapper.map(appointment, AppointmentRequestDetailResponseDto.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptAppointmentRequest(String email,Long id,String content, String status) throws Exception {
        User user = userRepository.findByEmail(email).get();
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(()-> new CalPickException(ErrorCode.APPOINTMENT_NOT_FOUND));
        if(appointment.getReceiver().getUserId() != user.getUserId()){
            throw new CalPickException(ErrorCode.NO_ACCESS_TO_APPOINTMENT_REQUEST);
        }
        if(status.equals("ACCEPT")){
            acceptRequest(appointment,user.getUserId(),id);
        }else if(status.equals("REJECT")){
            rejectRequest(appointment,user.getUserId(),id,content);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void acceptRequest(Appointment appointment,Long userId,Long id) throws Exception {

        appointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
        appointment.setModifiedAt(LocalDateTime.now());

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String date = appointment.getStartAt().format(formatter) + " ~ " + appointment.getEndAt().format(formatter);

        //수신자 수락 알림 메일 발송
        mailService.sendSimpleMessageAsync(appointment.getReceiver().getEmail(),appointment.getRequesterName(),appointment.getTitle(),notification.getNotificationId(),date,"","ACCEPT");

        String requesterEmail = "";

        if(appointment.getRequester()!=null){
            requesterEmail = appointment.getRequester().getEmail();
        }else{
            requesterEmail = appointment.getRequesterEmail();
        }

        //요청자 알림 메일 발송
        mailService.sendSimpleMessageAsync(requesterEmail,appointment.getReceiver().getName(),appointment.getTitle(),notification.getNotificationId(),date,"","ACCEPT");


    }


    @Transactional(rollbackFor = Exception.class)
    public void rejectRequest(Appointment appointment,Long userId,Long id, String content) throws Exception {
        appointment.setAppointmentStatus(AppointmentStatus.REJECTED);

        Notification notification = Notification.of(appointment,NotificationEvent.REJECT,content);
        notificationRepository.save(notification);

        NotificationType notificationType = NotificationType.of(com.example.calpick.domain.entity.enums.NotificationType.APPOINTMENT,appointment.getAppointmentId(),notification);
        notificationTypeRepository.save(notificationType);

        String requesterEmail = "";

        if(appointment.getRequester()!=null){
            requesterEmail = appointment.getRequester().getEmail();
        }else{
            requesterEmail = appointment.getRequesterEmail();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String date = appointment.getStartAt().format(formatter) + " ~ " + appointment.getEndAt().format(formatter);

        //요청자 알림 메일 발송
        mailService.sendSimpleMessageAsync(requesterEmail, appointment.getReceiver().getName(),appointment.getTitle(),notification.getNotificationId(),date,"","REJECT");
    }
}
