package com.example.calpick.domain.dto.request.friendRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseFriendRequestDto {
    public Long friendRequestId;
    public String status;
}
