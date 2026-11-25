package learning.event_ticketing_and_booking_api.DTO.Request;

import lombok.Data;

@Data
public class UserRequest {
    private  String email;
    private String password;
    private String name;
}
