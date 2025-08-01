package com.example.calpick.domain.repository;

import com.example.calpick.domain.entity.Appointment;
import com.example.calpick.domain.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    /*@Query("SELECT a FROM Appointment a WHERE a.receiver.userId = :receiverId AND a.appointmentStatus = :status")
    List<Appointment> findByReceiverIdAndStatus(@Param("receiverId") Long receiverId,
                                                @Param("status") AppointmentStatus status);*/


    @Query("""
    SELECT a FROM Appointment a
    WHERE a.receiver.userId = :userId AND a.appointmentStatus = :status
""")
    Page<Appointment> findByReceiverIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") AppointmentStatus status,
            Pageable pageable
    );

    @Query("""
    SELECT a FROM Appointment a
    WHERE a.receiver.userId = :userId AND a.appointmentStatus IN :statuses
""")
    Page<Appointment> findByReceiverIdAndStatuses(
            @Param("userId") Long userId,
            @Param("statuses") List<AppointmentStatus> statuses,
            Pageable pageable
    );

    @Query("""
    SELECT a FROM Appointment a
    WHERE a.requester.userId = :userId AND a.appointmentStatus IN :statuses
""")
    Page<Appointment> findByRequesterIdAndStatuses(
            @Param("userId") Long userId,
            @Param("statuses") List<AppointmentStatus> statuses,
            Pageable pageable
    );

    @Override
    Optional<Appointment> findById(Long id);
}
