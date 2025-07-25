package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.request.friendRequest.ResponseFriendRequestDto;
import com.example.calpick.domain.dto.response.friendRequest.*;
import com.example.calpick.domain.entity.*;
import com.example.calpick.domain.entity.enums.AppointmentStatus;
import com.example.calpick.domain.entity.enums.NotificationEvent;
import com.example.calpick.domain.entity.enums.RequestStatus;
import com.example.calpick.domain.repository.FriendRequestRepository;
import com.example.calpick.domain.repository.NotificationRepository;
import com.example.calpick.domain.repository.NotificationTypeRepository;
import com.example.calpick.domain.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final MailService mailService;


    @Transactional(rollbackFor = Exception.class)
    public void requestFriendRequest(String email,Long friendId) throws Exception {
        User user =  userRepository.findByEmail(email).get();

        User friend = userRepository.findById(friendId).orElseThrow(()->new CalPickException(ErrorCode.FRIEND_NOT_FOUND));

        if(friendRequestRepository.isDuplicatedFriendRequest(user.getUserId(),friendId)==1){ // 중복된 요청일 때,
            throw new CalPickException(ErrorCode.DUPLICATE_FRIEND_REQUEST);
        }

        if(friendRequestRepository.isExistedFriendRequest(user.getUserId(),friendId)==1){ //선택한 친구가 이미 친구 요청을 보냈을 때,
            throw new CalPickException(ErrorCode.FRIEND_REQUEST_ALREADY_RECEIVED);
        }

        if(friendRequestRepository.isAlreadyFriends(user.getUserId(),friendId)==1){//이미 친구 관계일때,
            throw new CalPickException(ErrorCode.ALREADY_FRIENDS);
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequester(user);
        friendRequest.setReceiver(friend);
        friendRequest.setRequestStatus(RequestStatus.REQUESTED);
        friendRequest.setCreatedAt(LocalDateTime.now());

        FriendRequest savedFriendRequest = friendRequestRepository.save(friendRequest);

        //수신자에게 친구 신청 알람 메일 전송
        Notification notification = Notification.of(user, friend, NotificationEvent.REQUEST,"친구 요청입니다");
        Notification savedNotification = notificationRepository.save(notification);


        NotificationType notificationType = NotificationType.of(com.example.calpick.domain.entity.enums.NotificationType.FRIEND,savedFriendRequest.getFriendRequestId(),savedNotification);
        notificationTypeRepository.save(notificationType);


        mailService.sendSimpleMessageAsync(friend.getEmail(),user.getName(),"",savedNotification.getNotificationId(),"","","requestFriend");
    }

    @Transactional(rollbackFor = Exception.class)
    public void responseFriendRequest(String email, ResponseFriendRequestDto dto) throws Exception {
        User user =  userRepository.findByEmail(email).get();
        FriendRequest friendRequest = friendRequestRepository.findById(dto.friendRequestId)
                .orElseThrow(()->new CalPickException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if(friendRequest.getReceiver().getUserId()!=user.getUserId()){
            throw new CalPickException(ErrorCode.NO_ACCESS_TO_FRIEND_REQUEST);
        }

        User friend = userRepository.findById(friendRequest.getRequester().getUserId()).orElseThrow(()->new CalPickException(ErrorCode.FRIEND_NOT_FOUND));

        if(dto.getStatus().equals("ACCEPT")){
            acceptRequest(friendRequest,user,friend);
        }else if(dto.getStatus().equals("REJECT")){
            rejectRequest(friendRequest,user,friend);
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public void acceptRequest(FriendRequest friendRequest,User receiver, User requester) throws Exception {

        friendRequest.setRequestStatus(RequestStatus.ACCEPTED);

        Notification notification = Notification.of(requester,receiver,NotificationEvent.ACCEPT,"친구가 추가되었습니다");
        notificationRepository.save(notification);


        NotificationType notificationType = NotificationType.of(com.example.calpick.domain.entity.enums.NotificationType.FRIEND,friendRequest.getFriendRequestId(),notification);
        notificationTypeRepository.save(notificationType);


        //수신자 친구 확정 메일 발송
        mailService.sendSimpleMessageAsync(receiver.getEmail(),requester.getName(),"",notification.getNotificationId(),"","","acceptFriend");


        //요청자 친구 확정 메일 발송
        mailService.sendSimpleMessageAsync(requester.getEmail(),receiver.getName(),"",notification.getNotificationId(),"","","acceptFriend");


    }


    @Transactional(rollbackFor = Exception.class)
    public void rejectRequest(FriendRequest friendRequest,User receiver, User requester) throws Exception {
        friendRequestRepository.delete(friendRequest);

        Notification notification = Notification.of(requester,receiver,NotificationEvent.REJECT,"친구 요청이 거절되었습니다");
        notificationRepository.save(notification);

        NotificationType notificationType = NotificationType.of(com.example.calpick.domain.entity.enums.NotificationType.FRIEND,friendRequest.getFriendRequestId(),notification);
        notificationTypeRepository.save(notificationType);

        //요청자 알림 메일 발송
        mailService.sendSimpleMessageAsync(requester.getEmail(), receiver.getName(),"",notification.getNotificationId(),"","","rejectFriend");
    }

    @Transactional
    public FriendRequestListResponseDto getFriendRequestsList(String email, int page, int size){
        User user = userRepository.findByEmail(email).get();

        Pageable pageable = PageRequest.of(page-1, size);
        Page<FriendRequestDto> pages = friendRequestRepository.getFriendRequestsList(user.getUserId(),pageable);
        List<FriendRequestDto> dtoList = pages.getContent();

        return FriendRequestListResponseDto.toResponseDto(page,pages.getTotalPages(),dtoList);
    }

    @Transactional
    public FriendsListResponseDto getFriendsList(String email,int page,int size){
        User user = userRepository.findByEmail(email).get();

        Pageable pageable = PageRequest.of(page-1, size);
        Page<FriendResponseDto> pages=  friendRequestRepository.getFriendList(user.getUserId(),pageable);
        List<FriendResponseDto> dtoList = pages.getContent();

       return FriendsListResponseDto.toResponseDto(page,pages.getTotalPages(),dtoList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFriendRequest(String email,Long friendRequestId){
        User user = userRepository.findByEmail(email).get();

        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(()->new CalPickException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if(friendRequest.getReceiver().getUserId()!=user.getUserId() && friendRequest.getRequester().getUserId() != user.getUserId()){
            throw new CalPickException(ErrorCode.NO_ACCESS_TO_FRIEND_REQUEST);
        }

        friendRequestRepository.delete(friendRequest);
    }

    @Transactional
    public UsersWithFriendStatusListResponseDto getUsersListWithFriendStatus(String email,String searchType, String query, int page, int size){
        User user = userRepository.findByEmail(email).get();
        Pageable pageable = PageRequest.of(page-1, size);
        Page<UserWithFriendStatusDto> dtoPage =
                friendRequestRepository.searchUsersWithFriendStatus(user.getUserId(), searchType, query, pageable)
                        .map(p -> new UserWithFriendStatusDto(
                                p.getId(),
                                p.getName(),
                                p.getEmail(),
                                p.getProfileUrl(),
                                p.getIsFriend() == 1,
                                p.getIsRequested() == 1
                        ));
        return UsersWithFriendStatusListResponseDto.toResponseDto(page,dtoPage.getTotalPages(),dtoPage.getContent());

    }


}
