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
public class FriendsListResponseDto {
    public int page;
    public int totalPages;
    public List<FriendResponseDto> friends;

    public static FriendsListResponseDto toResponseDto(int page , int totalPages, List<FriendResponseDto> friends){
        return new FriendsListResponseDto(page,totalPages,friends);
    }
}
