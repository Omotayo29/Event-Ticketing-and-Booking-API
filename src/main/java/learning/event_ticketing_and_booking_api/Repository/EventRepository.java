package learning.event_ticketing_and_booking_api.Repository;

import learning.event_ticketing_and_booking_api.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

}
