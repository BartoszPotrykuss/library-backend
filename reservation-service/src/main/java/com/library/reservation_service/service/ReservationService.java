package com.library.reservation_service.service;

import com.library.reservation_service.dto.ReservationRequest;
import com.library.reservation_service.entity.Reservation;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ReservationService {
    List<Reservation> getAllReservations(HttpServletRequest request);

    Reservation addReservation(ReservationRequest reservationRequest, HttpServletRequest request);
}
