package learning.event_ticketing_and_booking_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@RestController
public class EventTicketingAndBookingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventTicketingAndBookingApiApplication.class, args);
    }

    @GetMapping("/")
    public String home(){
        return "home";
    }

}
