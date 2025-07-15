package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ma.iga.biblio.entity.Payment;
import ma.iga.biblio.entity.PaymentType;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Payment data transfer object")
public class PaymentDto {
    @Schema(description = "Unique identifier of the payment")
    private Long id;

    @Schema(description = "Fine information")
    private FineDto fine;

    @Schema(description = "User information")
    private UserDto user;

    @Schema(description = "Payment amount")
    private Double amount;

    @Schema(description = "Type of payment")
    private PaymentType paymentType;

    @Schema(description = "Transaction ID")
    private String transactionId;

    @Schema(description = "Date when the payment was made")
    private LocalDateTime paymentDate;

    @Schema(description = "Next payment date (for partial payments)")
    private LocalDateTime nextPaymentDate;

    @Schema(description = "Whether this is a partial payment")
    private Boolean isPartial;

    @Schema(description = "Partial payment number")
    private Integer partialPaymentNumber;

    @Schema(description = "Total number of partial payments")
    private Integer totalPartialPayments;

    @Schema(description = "Additional notes")
    private String notes;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    public static PaymentDto fromEntity(Payment payment) {
        if (payment == null) return null;
        return PaymentDto.builder()
                .id(payment.getId())
                .fine(payment.getFine() != null ? FineDto.builder()
                    .id(payment.getFine().getId())
                    .amount(payment.getFine().getAmount())
                    .paidAmount(payment.getFine().getPaidAmount())
                    .status(payment.getFine().getStatus())
                    .fineDate(payment.getFine().getFineDate())
                    .dueDate(payment.getFine().getDueDate())
                    .build() : null)
                .user(payment.getUser() != null ? UserDto.builder()
                    .id(payment.getUser().getId())
                    .username(payment.getUser().getUsername())
                    .email(payment.getUser().getEmail())
                    .firstName(payment.getUser().getFirstName())
                    .lastName(payment.getUser().getLastName())
                    .build() : null)
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .nextPaymentDate(payment.getNextPaymentDate())
                .isPartial(payment.getIsPartial())
                .partialPaymentNumber(payment.getPartialPaymentNumber())
                .totalPartialPayments(payment.getTotalPartialPayments())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public static Payment toEntity(PaymentDto dto) {
        if (dto == null) return null;
        Payment payment = new Payment();
        payment.setId(dto.getId());
        // Note: Fine and User should be set by the service layer with proper entities from repository
        payment.setAmount(dto.getAmount());
        payment.setPaymentType(dto.getPaymentType());
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setNextPaymentDate(dto.getNextPaymentDate());
        payment.setIsPartial(dto.getIsPartial());
        payment.setPartialPaymentNumber(dto.getPartialPaymentNumber());
        payment.setTotalPartialPayments(dto.getTotalPartialPayments());
        payment.setNotes(dto.getNotes());
        return payment;
    }
} 