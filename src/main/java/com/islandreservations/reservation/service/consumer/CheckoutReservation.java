package com.islandreservations.reservation.service.consumer;

import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.model.Reservation;
import com.islandreservations.reservation.model.ReservationStatus;
import com.islandreservations.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public record CheckoutReservation(
        ReservationRepository reservationRepository) {

    public void execute(ReservationDTO reservationDTO) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationDTO.getId());
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            if (ReservationStatus.CHECKOUT_REQUESTED.equals(reservation.getReservationStatus())) {
                reservation.setReservationStatus(ReservationStatus.COMPLETED);
                reservationRepository.save(reservation);
            }
        }
    }
}
