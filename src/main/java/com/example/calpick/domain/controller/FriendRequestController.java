package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.request.friendRequest.ResponseFriendRequestDto;
import com.example.calpick.domain.dto.request.friendRequest.SaveFriendRequestDto;
import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.response.friendRequest.FriendRequestListResponseDto;
import com.example.calpick.domain.dto.response.friendRequest.FriendsListResponseDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.FriendRequestService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @PostMapping("/requests")
    public Response<Object> requestFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody SaveFriendRequestDto dto){ //친구 요청
        if(userDetails == null){
            throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        }
        friendRequestService.requestFriendRequest(userDetails.getEmail(), dto.friendId);
        return Response.success();
    }


    @GetMapping("/requests")
    public Response<FriendRequestListResponseDto> getFriendRequestsList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                        @RequestParam(name = "page", defaultValue = "1")int page,
                                                                        @RequestParam(name = "size", defaultValue = "10")int size){ //친구 요청 목록 조회
        if(userDetails == null){
            throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        }
        return Response.success(friendRequestService.getFriendRequestsList(userDetails.getEmail(),page,size));
    }


    @PutMapping("/requests")
    public Response<Object> responseFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ResponseFriendRequestDto dto){ //친구 요청 수락/거절
        if(userDetails == null){
            throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        }
        friendRequestService.responseFriendRequest(userDetails.getEmail(),dto);
        return Response.success();
    }


    @GetMapping
    public Response<FriendsListResponseDto> getFriendsList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "page", defaultValue = "1")int page,
                                                           @RequestParam(name = "size", defaultValue = "10")int size){ //친구 목록 조회
        if(userDetails == null){
            throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        }
        return Response.success(friendRequestService.getFriendsList(userDetails.getEmail(),page,size));
    }


    @DeleteMapping("/{friendRequestId}")
    public Response<Object> deleteFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("friendRequestId")Long friendRequestId){ //친구 삭제
        if(userDetails == null){
            throw new CalPickException(ErrorCode.UNAUTHORIZED_USER);
        }
        friendRequestService.deleteFriendRequest(userDetails.getEmail(),friendRequestId);
        return Response.success();

    }

    @GetMapping("/users")
    public void getUsersList(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam(name = "searchType")String searchType,
                             @RequestParam(name = "query")String query,
                             @RequestParam(name = "page", defaultValue = "1")int page,
                             @RequestParam(name = "size", defaultValue = "10")int size){ //검색한 유저 목록 조회

    }
}
