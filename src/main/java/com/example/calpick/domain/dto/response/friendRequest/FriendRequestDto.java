package com.example.calpick.domain.dto.response.friendRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendRequestDto {
    public Long friendRequestId;
    public String email;
    public String name;
    public String profileUrl;
}
