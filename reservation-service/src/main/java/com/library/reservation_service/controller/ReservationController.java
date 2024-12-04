package com.library.reservation_service.controller;

import com.library.reservation_service.dto.ReservationRequest;
import com.library.reservation_service.entity.Reservation;
import com.library.reservation_service.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations(HttpServletRequest request) {
        List<Reservation> reservationList = reservationService.getAllReservations(request);
        return new ResponseEntity<>(reservationList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Reservation> addReservation(
            @RequestBody ReservationRequest reservationRequest,
            HttpServletRequest request) {
        Reservation newReservation = reservationService.addReservation(reservationRequest, request);
        return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
    }

}
