package learning.event_ticketing_and_booking_api.Service;

import learning.event_ticketing_and_booking_api.DTO.Request.UserRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.UserResponse;
import learning.event_ticketing_and_booking_api.Repository.UserRepository;
import learning.event_ticketing_and_booking_api.mappers.Mapping;
import learning.event_ticketing_and_booking_api.model.User;
import learning.event_ticketing_and_booking_api.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private Mapping mapping;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private BCryptPasswordEncoder  encoder = new BCryptPasswordEncoder(12);

    public UserResponse register(UserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setName(userRequest.getName());
        user.setPasswordHash(encoder.encode(userRequest.getPassword()));
        user.setRole(UserRole.ATTENDEE);
        userRepository.save(user);
        return mapping.UserRequestToUserDTO(user);
    }

    public String verify(UserRequest userRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getName(),userRequest.getPassword()));
        if(authentication.isAuthenticated())
            return jwtService.generateToken(userRequest.getName());
        return "fail";
    }
}
