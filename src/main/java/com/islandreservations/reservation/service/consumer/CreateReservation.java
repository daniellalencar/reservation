package com.islandreservations.reservation.service.consumer;

import com.islandreservations.reservation.ErrorMessage;
import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.model.Reservation;
import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class CreateReservation {
    private final ReservationRepository reservationRepository;

    @Transactional
    public void execute(ReservationDTO reservationDTO) {
        reservationRepository.findById(reservationDTO.getId())
                .ifPresentOrElse(
                        reservation -> confirmReservation(reservation),
                        () -> log.error(ErrorMessage.RESERVATION_NOT_FOUND.getMessage()));
    }

    private void confirmReservation(Reservation reservation) {
        if (ReservationStatus.REQUESTED.equals(reservation.getReservationStatus())) {
            reservation.setReservationStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        } else {
            log.error(ErrorMessage.RESERVATION_NOT_PENDING.getMessage());
        }
    }
}
