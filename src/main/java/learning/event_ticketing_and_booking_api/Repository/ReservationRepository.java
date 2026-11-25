package learning.event_ticketing_and_booking_api.Repository;

import learning.event_ticketing_and_booking_api.model.Reservation;
import learning.event_ticketing_and_booking_api.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    int countByUserIdAndStatus(Long userId, ReservationStatus reservationStatus);

    List<Reservation> findReservationByStatusAndExpiresAtLessThanEqual(ReservationStatus status, OffsetDateTime expiresAtIsLessThan);
}
