package com.islandreservations.reservation.service;

import com.islandreservations.reservation.dto.ReservationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationService {
    ReservationDTO createReservation(ReservationDTO reservationDTO);

    ReservationDTO cancelReservation(UUID reservationId);

    ReservationDTO updateReservation(ReservationDTO updatedReservationDTO);

    ReservationDTO checkIn(ReservationDTO reservationDTO);

    ReservationDTO checkOut(UUID reservationId);

    ReservationDTO getReservationById(UUID reservationId);

    Page<ReservationDTO> getAllReservations(Pageable pageable);

    List<LocalDate> getAvailableDates(LocalDate startDate, LocalDate endDate);
}
