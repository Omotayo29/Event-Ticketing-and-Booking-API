package learning.event_ticketing_and_booking_api.Exception;

public class PendingReservationException extends RuntimeException {
    public PendingReservationException(String message) {
        super(message);
    }
}
