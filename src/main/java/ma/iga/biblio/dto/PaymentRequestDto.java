package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ma.iga.biblio.entity.PaymentType;

@Data
@Builder
@Schema(description = "Payment request data transfer object")
public class PaymentRequestDto {
    @Schema(description = "Amount to pay", required = true)
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    private Double amount;

    @Schema(description = "Payment type", required = true)
    @NotNull(message = "Payment type is required")
    private PaymentType paymentType;

    @Schema(description = "Transaction ID for reference")
    private String transactionId;

    @Schema(description = "Whether this is a partial payment")
    private Boolean isPartial;

    @Schema(description = "Additional notes")
    private String notes;
} 