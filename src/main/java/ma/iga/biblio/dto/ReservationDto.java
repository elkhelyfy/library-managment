package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ma.iga.biblio.entity.Reservation;
import ma.iga.biblio.entity.ReservationStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Reservation data transfer object")
public class ReservationDto {
    @Schema(description = "Unique identifier of the reservation")
    private Long id;

    @Schema(description = "User information")
    private UserDto user;

    @Schema(description = "Book information")
    private BookDto book;

    @Schema(description = "Date when the reservation was made")
    private LocalDateTime reservationDate;

    @Schema(description = "Expected fulfillment date")
    private LocalDateTime expectedFulfillmentDate;

    @Schema(description = "Actual fulfillment date")
    private LocalDateTime actualFulfillmentDate;

    @Schema(description = "Expiration date of the reservation")
    private LocalDateTime expirationDate;

    @Schema(description = "Status of the reservation")
    private ReservationStatus status;

    @Schema(description = "Additional notes")
    private String notes;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static ReservationDto fromEntity(Reservation reservation) {
        if (reservation == null) return null;
        return ReservationDto.builder()
                .id(reservation.getId())
                .user(reservation.getUser() != null ? UserDto.builder()
                    .id(reservation.getUser().getId())
                    .username(reservation.getUser().getUsername())
                    .email(reservation.getUser().getEmail())
                    .firstName(reservation.getUser().getFirstName())
                    .lastName(reservation.getUser().getLastName())
                    .role(reservation.getUser().getRole())
                    .status(reservation.getUser().getStatus())
                    .build() : null)
                .book(reservation.getBook() != null ? BookDto.builder()
                    .id(reservation.getBook().getId())
                    .title(reservation.getBook().getTitle())
                    .isbn13(reservation.getBook().getIsbn13())
                    .isbn10(reservation.getBook().getIsbn10())
                    .build() : null)
                .reservationDate(reservation.getReservationDate())
                .expectedFulfillmentDate(reservation.getExpectedFulfillmentDate())
                .actualFulfillmentDate(reservation.getActualFulfillmentDate())
                .expirationDate(reservation.getExpirationDate())
                .status(reservation.getStatus())
                .notes(reservation.getNotes())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }

    public static Reservation toEntity(ReservationDto dto) {
        if (dto == null) return null;
        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        // Note: User and Book should be set by the service layer with proper entities from repository
        reservation.setReservationDate(dto.getReservationDate());
        reservation.setExpectedFulfillmentDate(dto.getExpectedFulfillmentDate());
        reservation.setActualFulfillmentDate(dto.getActualFulfillmentDate());
        reservation.setExpirationDate(dto.getExpirationDate());
        reservation.setStatus(dto.getStatus());
        reservation.setNotes(dto.getNotes());
        return reservation;
    }
} 