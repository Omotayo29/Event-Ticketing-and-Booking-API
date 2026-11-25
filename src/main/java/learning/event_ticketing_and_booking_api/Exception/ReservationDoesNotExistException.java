package learning.event_ticketing_and_booking_api.Exception;

public class ReservationDoesNotExistException extends RuntimeException {
    public ReservationDoesNotExistException(String message) {
        super(message);
    }
}
