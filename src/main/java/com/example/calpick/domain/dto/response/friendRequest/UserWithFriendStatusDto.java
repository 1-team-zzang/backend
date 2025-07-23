package com.example.calpick.domain.dto.response.friendRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserWithFriendStatusDto {
    public Long id;
    public String email;
    public String name;
    public String profileUrl;
    public Boolean isFriend;
}
