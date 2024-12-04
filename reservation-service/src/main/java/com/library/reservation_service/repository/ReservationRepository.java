package com.library.reservation_service.repository;

import com.library.reservation_service.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> getByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.isCancelled = TRUE WHERE r.endDateTime < CURRENT_TIMESTAMP AND r.isCancelled = FALSE")
    void updateIsCancelledReservations();
}
