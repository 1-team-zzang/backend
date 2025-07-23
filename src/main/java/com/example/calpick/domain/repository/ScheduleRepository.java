package com.example.calpick.domain.repository;

import com.example.calpick.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    @Query("SELECT s FROM Schedule s WHERE s.startAt < :endAt AND s.endAt > :startAt AND s.user.userId = :userId")
    List<Schedule> findOverlappingSchedules(@Param("startAt") LocalDateTime startAt,
                                            @Param("endAt") LocalDateTime endAt,
                                            @Param("userId") Long userId);

    Optional<Schedule> findScheduleByScheduleId(Long scheduleId);

    @Query("""
            SELECT s FROM Schedule s 
            WHERE s.user.userId = :userId 
            AND (
                (s.isRepeated = false AND s.startAt <= :endDate AND s.endAt >= :startDate)
                OR
                (s.isRepeated = true AND s.startAt <= :endDate AND s.repeatEndAt >= :startDate)
            )
            """)
    List<Schedule> getSchedulesByDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

}
