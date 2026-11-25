package learning.event_ticketing_and_booking_api.Repository;

import learning.event_ticketing_and_booking_api.model.TicketTiers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTierRepository extends JpaRepository<TicketTiers,Long> {
}
