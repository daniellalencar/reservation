package com.islandreservations.reservation.converter;

import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationConverter {
    public ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setFullName(reservation.getFullName());
        reservationDTO.setEmail(reservation.getEmail());
        reservationDTO.setStartDate(reservation.getStartDate());
        reservationDTO.setEndDate(reservation.getEndDate());
        reservationDTO.setId(reservation.getId());
        reservationDTO.setReservationStatus(reservation.getReservationStatus());
        reservationDTO.setIdempotencyKey(reservation.getIdempotencyKey());
        return reservationDTO;
    }

    public Reservation toModel(ReservationDTO dto) {
        Reservation reservation = new Reservation();
        reservation.setFullName(dto.getFullName());
        reservation.setEmail(dto.getEmail());
        reservation.setStartDate(dto.getStartDate());
        reservation.setEndDate(dto.getEndDate());
        reservation.setReservationStatus(dto.getReservationStatus());
        reservation.setIdempotencyKey(dto.getIdempotencyKey());
        return reservation;
    }
}
