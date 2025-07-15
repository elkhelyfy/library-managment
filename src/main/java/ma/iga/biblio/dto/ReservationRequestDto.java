package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Reservation request data transfer object")
public class ReservationRequestDto {
    @Schema(description = "ID of the book to reserve", required = true)
    @NotNull(message = "Book ID is required")
    private Long bookId;

    @Schema(description = "Additional notes for the reservation request")
    private String notes;

    @Schema(description = "Expected fulfillment date (optional)")
    private String expectedFulfillmentDate;
} 