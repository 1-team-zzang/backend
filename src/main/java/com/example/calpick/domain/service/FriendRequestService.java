package com.example.calpick.domain.service;

import com.example.calpick.domain.dto.request.friendRequest.ResponseFriendRequestDto;
import com.example.calpick.domain.dto.response.friendRequest.FriendRequestDto;
import com.example.calpick.domain.dto.response.friendRequest.FriendRequestListResponseDto;
import com.example.calpick.domain.dto.response.friendRequest.FriendResponseDto;
import com.example.calpick.domain.dto.response.friendRequest.FriendsListResponseDto;
import com.example.calpick.domain.entity.FriendRequest;
import com.example.calpick.domain.entity.User;
import com.example.calpick.domain.entity.enums.RequestStatus;
import com.example.calpick.domain.repository.FriendRequestRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Transactional(rollbackFor = Exception.class)
    public void requestFriendRequest(String email,Long friendId){
        User user =  userRepository.findByEmail(email).get();

        User friend = userRepository.findById(friendId).orElseThrow(()->new CalPickException(ErrorCode.FRIEND_NOT_FOUND));

        if(friendRequestRepository.isDuplicatedFriendRequest(user.getUserId(),friendId)){ // 중복된 요청일 때,
            throw new CalPickException(ErrorCode.DUPLICATE_FRIEND_REQUEST);
        }

        if(friendRequestRepository.isExistedFriendRequest(user.getUserId(),friendId)){ //선택한 친구가 이미 친구 요청을 보냈을 때,
            throw new CalPickException(ErrorCode.FRIEND_REQUEST_ALREADY_RECEIVED);
        }

        if(friendRequestRepository.isAlreadyFriends(user.getUserId(),friendId)){//이미 친구 관계일때,
            throw new CalPickException(ErrorCode.ALREADY_FRIENDS);
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequester(user);
        friendRequest.setReceiver(friend);
        friendRequest.setRequestStatus(RequestStatus.REQUESTED);

        friendRequestRepository.save(friendRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    public void responseFriendRequest(String email, ResponseFriendRequestDto dto){
        User user =  userRepository.findByEmail(email).get();
        FriendRequest friendRequest = friendRequestRepository.findById(dto.friendRequestId)
                .orElseThrow(()->new CalPickException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if(friendRequest.getReceiver().getUserId()!=user.getUserId()){
            throw new CalPickException(ErrorCode.NO_ACCESS_TO_FRIEND_REQUEST);
        }

        User friend = userRepository.findById(friendRequest.getRequester().getUserId()).orElseThrow(()->new CalPickException(ErrorCode.FRIEND_NOT_FOUND));

        if(dto.getStatus().equals("ACCEPT")){
            friendRequest.setRequestStatus(RequestStatus.ACCEPTED);
        }else if(dto.getStatus().equals("REJECT")){
            friendRequestRepository.delete(friendRequest);
        }
    }

    @Transactional
    public FriendRequestListResponseDto getFriendRequestsList(String email, int page, int size){
        User user = userRepository.findByEmail(email).get();

        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FriendRequestDto> pages = friendRequestRepository.getFriendRequestsList(user.getUserId(),pageable);
        List<FriendRequestDto> dtoList = pages.getContent();

        return FriendRequestListResponseDto.toResponseDto(page,pages.getTotalPages(),dtoList);
    }

    @Transactional
    public FriendsListResponseDto getFriendsList(String email,int page,int size){
        User user = userRepository.findByEmail(email).get();

        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FriendResponseDto> pages=  friendRequestRepository.getFriendList(user.getUserId(),pageable);
        List<FriendResponseDto> dtoList = pages.getContent();

       return FriendsListResponseDto.toResponseDto(page,size,dtoList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFriendRequest(String email,Long friendRequestId){
        User user = userRepository.findByEmail(email).get();

        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(()->new CalPickException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if(friendRequest.getReceiver().getUserId()!=user.getUserId()){
            throw new CalPickException(ErrorCode.NO_ACCESS_TO_FRIEND_REQUEST);
        }

        User friend = userRepository.findById(friendRequest.getRequester().getUserId()).orElseThrow(()->new CalPickException(ErrorCode.FRIEND_NOT_FOUND));

        friendRequestRepository.delete(friendRequest);
    }

    @Transactional
    public void getUsersListWithFriendStatus(String searchType, String query, int page, int size){
        if(searchType.equals("NAME")){

        }else if(searchType.equals("EMAIL")){

        }
    }


}
