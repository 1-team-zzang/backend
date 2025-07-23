package com.example.calpick.domain.dto.response.friendRequest;

public interface UserWithFriendStatusProjection {
    Long getId();
    String getName();
    String getEmail();
    String getProfileUrl();
    Integer getIsFriend();
}
