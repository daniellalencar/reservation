package com.islandreservations.reservation.service.consumer;

import com.islandreservations.reservation.dto.ReservationDTO;
import com.islandreservations.reservation.service.ReservationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Service
public class ReservationConsumer {

    private final CreateReservation createReservation;
    private final CheckinReservation checkinReservation;
    private final CheckoutReservation checkoutReservation;
    private final CancelReservation cancelReservation;

    @KafkaListener(topics = "${topic.create-reservation}", groupId = "group_id")
    public void createReservation(@Payload ReservationDTO reservationDTO) throws IOException {
        log.info(String.format("Create reservation {}", reservationDTO));
        createReservation.execute(reservationDTO);
    }

    @KafkaListener(topics = "${topic.checkout-reservation}", groupId = "group_id")
    public void checkoutReservation(@Payload ReservationDTO reservationDTO) throws IOException {
        log.info(String.format("Consuming checkout reservation {}", reservationDTO));
        checkoutReservation.execute(reservationDTO);
    }

    @KafkaListener(topics = "${topic.checkin-reservation}", groupId = "group_id")
    public void checkinReservation(@Payload ReservationDTO reservationDTO) throws IOException {
        log.info(String.format("Consuming checkin  reservation {}", reservationDTO));
        checkinReservation.execute(reservationDTO);
    }

    @KafkaListener(topics = "${topic.cancel-reservation}", groupId = "group_id")
    public void cancelReservation(@Payload ReservationDTO reservationDTO) throws IOException {
        log.info(String.format("Consuming checkin  reservation {}", reservationDTO));
        cancelReservation.execute(reservationDTO);
    }
}
