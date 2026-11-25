package learning.event_ticketing_and_booking_api.Repository;

import learning.event_ticketing_and_booking_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    User findByName(String username);
}
