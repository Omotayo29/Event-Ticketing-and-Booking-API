package learning.event_ticketing_and_booking_api.DTO.Response;

public record FieldValidationError(
        String field,
        String message
) {
}
