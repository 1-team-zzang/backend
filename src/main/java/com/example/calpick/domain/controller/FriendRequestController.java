package com.example.calpick.domain.controller;

import com.example.calpick.domain.dto.request.friendRequest.ResponseFriendRequestDto;
import com.example.calpick.domain.dto.request.friendRequest.SaveFriendRequestDto;
import com.example.calpick.domain.dto.response.Response;
import com.example.calpick.domain.dto.response.friendRequest.FriendRequestListResponseDto;
import com.example.calpick.domain.dto.response.friendRequest.FriendsListResponseDto;
import com.example.calpick.domain.dto.response.friendRequest.UsersWithFriendStatusListResponseDto;
import com.example.calpick.domain.dto.user.CustomUserDetails;
import com.example.calpick.domain.service.FriendRequestService;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friends")
@Tag(name = "FriendRequest", description = "FriendRequest API")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @PostMapping("/requests")
    public Response<Object> requestFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody SaveFriendRequestDto dto) throws Exception {
        friendRequestService.requestFriendRequest(userDetails.getEmail(), dto.friendId);
        return Response.success();
    }


    @GetMapping("/requests")
    public Response<FriendRequestListResponseDto> getFriendRequestsList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                        @RequestParam(name = "page", defaultValue = "1")int page,
                                                                        @RequestParam(name = "size", defaultValue = "10")int size){
        return Response.success(friendRequestService.getFriendRequestsList(userDetails.getEmail(),page,size));
    }


    @PutMapping("/requests")
    public Response<Object> responseFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ResponseFriendRequestDto dto) throws Exception {
        friendRequestService.responseFriendRequest(userDetails.getEmail(),dto);
        return Response.success();
    }


    @GetMapping
    public Response<FriendsListResponseDto> getFriendsList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @RequestParam(name = "page", defaultValue = "1")int page,
                                                           @RequestParam(name = "size", defaultValue = "10")int size){
        return Response.success(friendRequestService.getFriendsList(userDetails.getEmail(),page,size));
    }


    @DeleteMapping("/{friendRequestId}")
    public Response<Object> deleteFriendRequest(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("friendRequestId")Long friendRequestId){
        friendRequestService.deleteFriendRequest(userDetails.getEmail(),friendRequestId);
        return Response.success();

    }

    @GetMapping("/users")
    public Response<UsersWithFriendStatusListResponseDto> getUsersList(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                      @RequestParam(name = "searchType")String searchType,
                                                                      @RequestParam(name = "query")String query,
                                                                      @RequestParam(name = "page", defaultValue = "1")int page,
                                                                      @RequestParam(name = "size", defaultValue = "10")int size){
        return Response.success(friendRequestService.getUsersListWithFriendStatus(userDetails.getEmail(),searchType,query,page,size));
    }
}
