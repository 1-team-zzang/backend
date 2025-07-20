package com.example.calpick.domain.dto.response.friendRequest;

import com.example.calpick.domain.dto.response.appointment.AppointmentRequestListResponseDto;
import com.example.calpick.domain.dto.response.appointment.AppointmentRequestsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendRequestListResponseDto {

    public int page;
    public int totalPages;
    public List<FriendRequestDto> friendRequests;

    public static FriendRequestListResponseDto toResponseDto(int page , int totalPages, List<FriendRequestDto> friendRequests){
        return new FriendRequestListResponseDto(page,totalPages,friendRequests);
    }
}
