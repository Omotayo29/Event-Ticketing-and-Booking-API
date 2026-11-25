package learning.event_ticketing_and_booking_api.Exception;

public class AvailableCountException extends RuntimeException {
    public AvailableCountException(String message) {
        super(message);
    }
}
