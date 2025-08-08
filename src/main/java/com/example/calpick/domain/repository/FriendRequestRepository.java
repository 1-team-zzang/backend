package com.example.calpick.domain.repository;

import com.example.calpick.domain.dto.response.friendRequest.FriendRequestDto;
import com.example.calpick.domain.dto.response.friendRequest.FriendResponseDto;
import com.example.calpick.domain.dto.response.friendRequest.UserWithFriendStatusDto;
import com.example.calpick.domain.dto.response.friendRequest.UserWithFriendStatusProjection;
import com.example.calpick.domain.entity.FriendRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest,Long> {

     @Query(value = """
         SELECT * FROM (
             SELECT fr.friend_request_id AS friendRequestId,
                    u.user_id AS userId,
                    u.name AS name,
                    u.email AS email,
                    u.profile_url AS profileUrl
             FROM friend_request fr
             JOIN users u ON fr.receiver_id = u.user_id
             WHERE fr.request_status = 'ACCEPTED'
               AND fr.requester_id = :userId
               AND u.user_status <> 'DELETED'
     
             UNION ALL
     
             SELECT fr.friend_request_id AS friendRequestId,
                    u.user_id AS userId,
                    u.name AS name,
                    u.email AS email,
                    u.profile_url As profileUrl
             FROM friend_request fr
             JOIN users u ON fr.requester_id = u.user_id
             WHERE fr.request_status = 'ACCEPTED'
               AND fr.receiver_id = :userId
               AND u.user_status <> 'DELETED'

         ) AS friends
         ORDER BY name,email ASC
         """,  countQuery = """
            SELECT 
            (
                 SELECT COUNT(*) FROM friend_request fr
                 JOIN users u ON fr.receiver_id = u.user_id
                 WHERE fr.request_status = 'ACCEPTED'
                   AND fr.requester_id = :userId
                   AND u.user_status <> 'DELETED'

            ) + 
            (
               SELECT COUNT(*) FROM friend_request fr
               JOIN users u ON fr.requester_id = u.user_id
               WHERE fr.request_status = 'ACCEPTED'
               AND fr.receiver_id = :userId 
               AND u.user_status <> 'DELETED'

            ) 
         """,
             nativeQuery = true)
     Page<FriendResponseDto> getFriendList(@Param("userId") Long userId, Pageable pageable); //친구 목록 조회


     @Query(value = """
         SELECT EXISTS (
             SELECT 1
             FROM friend_request fr
             WHERE fr.requester_id = :userId
               AND fr.receiver_id = :receiverId
               AND fr.request_status = 'REQUESTED'
         )
     """, nativeQuery = true)
     Integer isDuplicatedFriendRequest(@Param("userId") Long userId, @Param("receiverId") Long receiverId);

     @Query(value = """
         SELECT EXISTS (
             SELECT 1
             FROM friend_request fr
             WHERE fr.requester_id = :receiverId
               AND fr.receiver_id = :userId
               AND fr.request_status = 'REQUESTED'
         )
     """, nativeQuery = true)
     Integer isExistedFriendRequest(@Param("userId") Long userId, @Param("receiverId") Long receiverId);


     @Query(value = """
         SELECT EXISTS (
             SELECT 1
             FROM friend_request fr
             WHERE fr.request_status = 'ACCEPTED'
               AND (
                 (fr.requester_id = :userId AND fr.receiver_id = :friendId)
                 OR
                 (fr.requester_id = :friendId AND fr.receiver_id = :userId)
               )
         )
     """, nativeQuery = true)
     Integer isAlreadyFriends(@Param("userId") Long userId, @Param("friendId") Long friendId);

     @Query(value = """
     SELECT fr.friend_request_id AS friendRequestId,
            u.name AS name,
            u.email AS email,
            u.profile_url AS profileUrl
            
     FROM friend_request fr
     JOIN users u ON u.user_id = fr.requester_id
     WHERE fr.receiver_id = :userId AND fr.request_status = 'REQUESTED'
     AND u.user_status <> 'DELETED'
     ORDER BY fr.created_at DESC

     """, countQuery = """
        SELECT COUNT(*)
        FROM friend_request fr
        JOIN users u ON u.user_id = fr.requester_id
        WHERE fr.receiver_id = :userId 
        AND fr.request_status = 'REQUESTED'
        AND u.user_status <> 'DELETED'
        """,
             nativeQuery = true)
     Page<FriendRequestDto> getFriendRequestsList(@Param("userId") Long userId, Pageable pageable);

     @Query(value = """
        SELECT
        u.user_id AS id,
        u.name AS name,
        u.email AS email,
        u.profile_url AS profileUrl,
        CASE
          WHEN fr.friend_request_id IS NOT NULL THEN 1
          ELSE 0
        END AS isFriend,
        CASE
          WHEN req.friend_request_id IS NOT NULL THEN 1
          ELSE 0
        END AS isRequested
        FROM users u
        LEFT JOIN friend_request fr
        ON fr.request_status = 'ACCEPTED'
        AND (
          (fr.requester_id = :userId AND fr.receiver_id = u.user_id)
          OR
          (fr.requester_id = u.user_id AND fr.receiver_id = :userId)
        )
        LEFT JOIN friend_request req
         ON req.request_status = 'REQUESTED'
         AND (
           (req.receiver_id = :userId AND req.requester_id = u.user_id)
           OR
           (req.receiver_id = u.user_id AND req.requester_id = :userId)
        )
        WHERE u.user_status <> 'DELETED'
        AND ( (:searchType = 'NAME' AND u.name LIKE CONCAT('%', :query, '%'))
             OR (:searchType = 'EMAIL' AND u.email LIKE CONCAT('%', :query, '%'))
             )
        ORDER BY u.name ASC, u.email ASC
         """,
             countQuery = """
               SELECT COUNT(*)
               FROM users u
               WHERE u.user_status <> 'DELETED'
               AND (
                (:searchType = 'NAME' AND u.name LIKE CONCAT('%', :query, '%'))
                OR (:searchType = 'EMAIL' AND u.email LIKE CONCAT('%', :query, '%'))
               )
         """,
             nativeQuery = true)
     Page<UserWithFriendStatusProjection> searchUsersWithFriendStatus(
             @Param("userId") Long userId,
             @Param("searchType") String searchType,
             @Param("query") String query,
             Pageable pageable
     );


}
