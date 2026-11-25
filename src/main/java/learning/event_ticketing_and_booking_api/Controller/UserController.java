package learning.event_ticketing_and_booking_api.Controller;

import learning.event_ticketing_and_booking_api.DTO.Request.UserRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.UserResponse;
import learning.event_ticketing_and_booking_api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest user) {
        return new ResponseEntity<>(userService.register(user), HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody UserRequest user) {
        return new ResponseEntity<>(userService.verify(user),HttpStatus.OK);
    }

}
