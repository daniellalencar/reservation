package com.islandreservations.reservation.service.consumer;

import com.islandreservations.reservation.ErrorMessage;
import com.islandreservations.reservation.converter.ReservationConverter;
import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.exception.InvalidReservationStatusException;
import com.islandreservations.reservation.exception.ReservationNotFoundException;
import com.islandreservations.reservation.model.Reservation;
import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.islandreservations.reservation.ErrorMessage.CANT_UPDATE_NON_CONFIRMED_RESERVATION;

@Slf4j
@AllArgsConstructor
public class UpdateReservation {
    private final ReservationRepository reservationRepository;
    private final ReservationConverter reservationConverter;

    @Transactional
    public void execute(ReservationDTO reservationDTO) {
        reservationRepository.findById(reservationDTO.getId()).
                ifPresentOrElse(reservation -> updateReservation(reservationDTO, reservation), throwReservationNotFoundException());
    }

    private Runnable throwReservationNotFoundException() {
        return () -> {
            throw new ReservationNotFoundException(ErrorMessage.RESERVATION_NOT_FOUND.getMessage());
        };
    }

    private void updateReservation(ReservationDTO reservationDTO, Reservation reservation) {
        if (ReservationStatus.CONFIRMED.equals(reservation.getReservationStatus())) {
            Reservation updatedReservation = reservationConverter.toModel(reservationDTO);
            reservationRepository.save(updatedReservation);
        } else {
            throw new InvalidReservationStatusException(CANT_UPDATE_NON_CONFIRMED_RESERVATION.getMessage());
        }
    }

}
