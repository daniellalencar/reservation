package com.islandreservations.reservation.repository;

import com.islandreservations.reservation.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, UUID> {

    Page<Reservation> findAll(Pageable pageable);

    Optional<Reservation> findById(UUID reservationId);

    Optional<Reservation> findByIdempotencyKey(UUID idempotencyKey);

    @Query("SELECT r FROM Reservation r WHERE r.startDate <= :endDate AND r.endDate >= :startDate")
    List<Reservation> findReservationsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
