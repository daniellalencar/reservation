package com.islandreservations.reservation.service.consumer;

import com.islandreservations.reservation.ErrorMessage;
import com.islandreservations.reservation.converter.ReservationConverter;
import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.model.Reservation;
import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CancelReservation {
    private final ReservationRepository reservationRepository;
    private final ReservationConverter reservationConverter;

    public void execute(ReservationDTO reservationDTO) {
        UUID reservationId = reservationDTO.getId();
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (ReservationStatus.CANCEL_REQUESTED.equals(reservation.getReservationStatus())) {
                reservation.setReservationStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
            }
        } else {
            log.error(ErrorMessage.RESERVATION_NOT_FOUND.getMessage());
        }
    }
}
