package learning.event_ticketing_and_booking_api.Exception;

import jakarta.servlet.http.HttpServletRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.ErrorResponse;
import learning.event_ticketing_and_booking_api.DTO.Response.FieldValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleEventDoesNotExistException(EventDoesNotExistException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(ReservationAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponse> handleReservationAlreadyProcessedException(ReservationAlreadyProcessedException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT,exception.getMessage(),request);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateException(InvalidDateException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST,exception.getMessage(),request);
    }

    @ExceptionHandler(AvailableCountException.class)
    public ResponseEntity<ErrorResponse> handleAvailableCountException(AvailableCountException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST,exception.getMessage(),request);
    }

    @ExceptionHandler(PendingReservationException.class)
    public ResponseEntity<ErrorResponse> handlePendingReservationException(PendingReservationException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.CONFLICT,exception.getMessage(),request);
    }

    @ExceptionHandler(ReservationDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleReservationDoesNotExistException(ReservationDoesNotExistException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND,exception.getMessage(),request);
    }

    @ExceptionHandler(TierDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleTierDoesNotExistException(TierDoesNotExistException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND,exception.getMessage(),request);
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleUserDoesNotExistException(UserDoesNotExistException exception, HttpServletRequest request){
        return buildResponse(HttpStatus.BAD_REQUEST,exception.getMessage(),request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){
        List<FieldValidationError> validationErrors = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.add(new FieldValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                )));
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getRequestURI(),
                validationErrors
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            HttpServletRequest request
    ) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                status.value(),
                message,
                request.getRequestURI(),
                List.of()
        );

        return ResponseEntity.status(status).body(response);
    }

}
