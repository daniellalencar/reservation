package com.islandreservations.reservation.service.consumer;

import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public record CheckinReservation(
        ReservationRepository reservationRepository) {

    public void execute(ReservationDTO reservationDTO) {
        reservationRepository.findById(reservationDTO.getId())
                .filter(reservation -> ReservationStatus.CHECKIN_REQUESTED.equals(reservation.getReservationStatus()))
                .ifPresent(reservation -> {
                    reservation.setReservationStatus(ReservationStatus.CHECKIN);
                    reservationRepository.save(reservation);
                });
    }
}
