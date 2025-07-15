package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Loan request data transfer object")
public class LoanRequestDto {
    @Schema(description = "ID of the book to borrow", required = true)
    @NotNull(message = "Book ID is required")
    private Long bookId;

    @Schema(description = "Additional notes for the loan request")
    private String notes;

    @Schema(description = "Requested loan duration in days (optional)")
    private Integer requestedDays;
} 