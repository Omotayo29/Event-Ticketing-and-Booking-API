package learning.event_ticketing_and_booking_api.DTO.Response;

import learning.event_ticketing_and_booking_api.model.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private String name;
    private String email;
    private UserRole userRole;
}
