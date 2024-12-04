package com.library.reservation_service.service;

import com.library.reservation_service.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    public ReservationScheduler(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Scheduled(fixedRate = 3600000)
    public void updateCancelledReservations() {
        reservationRepository.updateIsCancelledReservations();
    }
}
