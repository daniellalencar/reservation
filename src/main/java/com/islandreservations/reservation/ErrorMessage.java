package com.islandreservations.reservation;

public enum ErrorMessage {
    RESERVATION_NOT_PENDING("The reservation is no longer in pending status."),
    RESERVATION_NOT_FOUND("Reservation not found."),
    RESERVATION_ALREADY_CANCELED("Reservation already cancelled."),
    INVALID_RESERVATION_STATUS_FOR_CHECKIN("Invalid reservation status for check-in."),
    RESERVATION_IS_NOT_IN_CHECKED_IN("Reservation is not in checked-in status"),
    CANT_UPDATE_NON_CONFIRMED_RESERVATION("Can't update a reservation that is not confirmed"),
    RESERVATION_ALREADY_EXIST_WITH_SAME_IDEMPOTENCY("Reservation with this idempotency key already exists");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
