package com.example.calpick.domain.dto.response.friendRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsersWithFriendStatusListResponseDto {
    public int page;
    public int totalPages;
    public List<UserWithFriendStatusDto> users;

    public static UsersWithFriendStatusListResponseDto toResponseDto(int page , int totalPages, List<UserWithFriendStatusDto> users){
        return new UsersWithFriendStatusListResponseDto(page,totalPages,users);
    }


}
