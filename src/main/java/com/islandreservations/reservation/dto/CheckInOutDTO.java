package com.islandreservations.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class CheckInOutDTO {
    private UUID reservationId;
    private UUID idempotencyKey;
}
