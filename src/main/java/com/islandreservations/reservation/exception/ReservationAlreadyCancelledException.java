package com.islandreservations.reservation.exception;

public class ReservationAlreadyCancelledException extends RuntimeException {
    public ReservationAlreadyCancelledException(String message) {
        super(message);
    }
}
