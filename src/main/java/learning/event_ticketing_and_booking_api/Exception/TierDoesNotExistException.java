package learning.event_ticketing_and_booking_api.Exception;

public class TierDoesNotExistException extends RuntimeException {
    public TierDoesNotExistException(String message) {
        super(message);
    }
}
