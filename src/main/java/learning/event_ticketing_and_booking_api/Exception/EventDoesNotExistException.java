package learning.event_ticketing_and_booking_api.Exception;

public class EventDoesNotExistException extends RuntimeException {
    public EventDoesNotExistException(String message) {
        super(message);
    }
}
