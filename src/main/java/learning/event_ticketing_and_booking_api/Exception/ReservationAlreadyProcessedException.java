package learning.event_ticketing_and_booking_api.Exception;

public class ReservationAlreadyProcessedException extends RuntimeException {
    public ReservationAlreadyProcessedException(String message) {
        super(message);
    }
}
