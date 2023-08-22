package com.islandreservations.reservation.service.producer;

import com.islandreservations.reservation.dto.ReservationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReservationProducer {

    @Value("${topic.create-reservation}")
    private String createReservationTopic;

    @Value("${topic.cancel-reservation}")
    private String cancelReservationTopic;

    @Value("${topic.checkin-reservation}")
    private String checkInReservationTopic;

    @Value("${topic.checkout-reservation}")
    private String checkOutReservationTopic;

    @Value("${topic.update-reservation}")
    private String updateReservationTopic;

    private final KafkaTemplate<String, ReservationDTO> kafkaTemplate;

    public ReservationProducer(KafkaTemplate<String, ReservationDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void createReservation(@Payload ReservationDTO reservationDTO) {
        kafkaTemplate.send(createReservationTopic, reservationDTO);
    }

    public void cancelReservation(@Payload ReservationDTO cancelledReservationDTO) {
        kafkaTemplate.send(cancelReservationTopic, cancelledReservationDTO);
    }

    public void checkIn(ReservationDTO cancelledReservationDTO) {
        kafkaTemplate.send(checkInReservationTopic, cancelledReservationDTO);
    }

    public void checkOut(ReservationDTO reservationDTO) {
        kafkaTemplate.send(checkOutReservationTopic, reservationDTO);
    }

    public void updateReservation(ReservationDTO reservationDTO) {
        kafkaTemplate.send(updateReservationTopic, reservationDTO);
    }
}
