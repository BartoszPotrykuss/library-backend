package com.library.reservation_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    private Long roomId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
