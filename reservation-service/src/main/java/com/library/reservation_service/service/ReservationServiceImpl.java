package com.library.reservation_service.service;

import com.library.reservation_service.dto.ReservationRequest;
import com.library.reservation_service.entity.Reservation;
import com.library.reservation_service.entity.Room;
import com.library.reservation_service.repository.ReservationRepository;
import com.library.reservation_service.repository.RoomRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService{

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Reservation> getAllReservations(HttpServletRequest request) {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation addReservation(ReservationRequest reservationRequest, HttpServletRequest request) {
        Room room = roomRepository.findById(reservationRequest.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room with id " + reservationRequest.getRoomId() + " does not exist"));

        String authorizationHeader = request.getHeader("Authorization");
        Claims claims = extractClaimsFromJwtToken(authorizationHeader);
        String username = claims.getSubject();

        Reservation newReservation = Reservation.builder()
                .room(room)
                .startDateTime(reservationRequest.getStartDateTime())
                .endDateTime(reservationRequest.getEndDateTime())
                .username(username)
                .isCancelled(false)
                .build();

        return reservationRepository.save(newReservation);
    }


    private Claims extractClaimsFromJwtToken(String authorizationHeader) {
        // Wyciągnij token JWT z nagłówka
        String jwtToken = authorizationHeader.substring(7);

        // Rozkoduj token i odczytaj dane
        return Jwts.parser()
                .setSigningKey("5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437") // Klucz symetryczny
                .parseClaimsJws(jwtToken)
                .getBody();
    }
}
