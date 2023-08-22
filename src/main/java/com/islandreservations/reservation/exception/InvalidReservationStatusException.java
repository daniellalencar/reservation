package com.islandreservations.reservation.exception;

public class InvalidReservationStatusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidReservationStatusException(String message) {
        super(message);
    }

    public InvalidReservationStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidReservationStatusException(Throwable cause) {
        super(cause);
    }

}
