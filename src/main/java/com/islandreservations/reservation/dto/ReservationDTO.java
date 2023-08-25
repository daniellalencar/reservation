package com.islandreservations.reservation.dto;

import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.validator.ValidReservation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@ToString
@Data
@NoArgsConstructor
@ValidReservation
public class ReservationDTO {
    @NotNull(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Arrival date is required")
    @Future(message = "Arrival date must be in the future")
    private LocalDate startDate;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDate endDate;

    private ReservationStatus reservationStatus;
    private UUID idempotencyKey;
    private UUID id;
}
