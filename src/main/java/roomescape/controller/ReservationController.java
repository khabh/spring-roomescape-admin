package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.ReservationSaveRequest;
import roomescape.dto.ReservationResponse;
import roomescape.model.Reservation;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert reservationInsert;
    private final AtomicLong id = new AtomicLong(1);
    private final List<Reservation> reservations = new ArrayList<>();

    public ReservationController(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.reservationInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservations")
                .usingGeneratedKeyColumns("id");
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        final String selectQuery = "SELECT id, name, date, time FROM reservations";
        final List<ReservationResponse> reservationResponses = jdbcTemplate.query(
                selectQuery, (selectedReservation, rowNum) -> {
                    final Reservation reservation = new Reservation(
                            selectedReservation.getLong("id"),
                            selectedReservation.getString("name"),
                            selectedReservation.getString("date"),
                            selectedReservation.getString("time")
                    );
                    return ReservationResponse.from(reservation);
                });
        return ResponseEntity.ok(reservationResponses);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> saveReservation(
            @RequestBody final ReservationSaveRequest reservationSaveRequest) {
        final Map<String, String> reservationParameters = Map.of(
                "name", reservationSaveRequest.name(),
                "date", reservationSaveRequest.date(),
                "time", reservationSaveRequest.time());
        final Long savedReservationId = reservationInsert.executeAndReturnKey(reservationParameters).longValue();
        final Reservation reservation = reservationSaveRequest.toReservation(savedReservationId);
        final ReservationResponse reservationResponse = ReservationResponse.from(reservation);
        return ResponseEntity.created(URI.create("/reservations/" + savedReservationId))
                .body(reservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(final @PathVariable("id") Long id) {
        final Optional<Reservation> findReservation = reservations.stream()
                .filter(reservation -> reservation.getId().equals(id))
                .findAny();
        if (findReservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        reservations.remove(findReservation.get());
        return ResponseEntity.noContent().build();
    }
}
