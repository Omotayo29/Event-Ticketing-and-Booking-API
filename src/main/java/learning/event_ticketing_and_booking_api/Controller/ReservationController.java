package learning.event_ticketing_and_booking_api.Controller;

import jakarta.validation.Valid;
import learning.event_ticketing_and_booking_api.DTO.Request.ReservationRequest;
import learning.event_ticketing_and_booking_api.DTO.Response.ReservationResponse;
import learning.event_ticketing_and_booking_api.Service.ReservationService;
import learning.event_ticketing_and_booking_api.model.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(reservationService.getReservationById(reservationId, principal.getId()), HttpStatus.OK);
    }

    @PatchMapping("reservations/{reservationId}/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return new ResponseEntity<>(reservationService.confirmReservation(reservationId, principal.getId()),HttpStatus.OK);
    }

    @PostMapping("events/{eventId}/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReservationRequest reservationRequest) {
        return new ResponseEntity<>(reservationService.createReservation(principal.getId(), eventId, reservationRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("reservations/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        reservationService.cancelReservation(reservationId, principal.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
