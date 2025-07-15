package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ma.iga.biblio.entity.Fine;
import ma.iga.biblio.entity.FineStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Fine data transfer object")
public class FineDto {
    @Schema(description = "Unique identifier of the fine")
    private Long id;

    @Schema(description = "Loan information")
    private LoanDto loan;

    @Schema(description = "Total amount of the fine")
    private Double amount;

    @Schema(description = "Amount already paid")
    private Double paidAmount;

    @Schema(description = "Number of days overdue")
    private Integer daysOverdue;

    @Schema(description = "Date when the fine was issued")
    private LocalDateTime fineDate;

    @Schema(description = "Due date for paying the fine")
    private LocalDateTime dueDate;

    @Schema(description = "Status of the fine")
    private FineStatus status;

    @Schema(description = "Additional notes")
    private String notes;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Whether the fine is fully paid")
    private Boolean isFullyPaid;

    @Schema(description = "Whether the fine is overdue")
    private Boolean isOverdue;

    @Schema(description = "Remaining amount to be paid")
    private Double remainingAmount;

    public static FineDto fromEntity(Fine fine) {
        if (fine == null) return null;
        return FineDto.builder()
                .id(fine.getId())
                .loan(fine.getLoan() != null ? LoanDto.builder()
                    .id(fine.getLoan().getId())
                    .loanDate(fine.getLoan().getLoanDate())
                    .dueDate(fine.getLoan().getDueDate())
                    .returnDate(fine.getLoan().getReturnDate())
                    .status(fine.getLoan().getStatus())
                    .build() : null)
                .amount(fine.getAmount())
                .paidAmount(fine.getPaidAmount())
                .daysOverdue(fine.getDaysOverdue())
                .fineDate(fine.getFineDate())
                .dueDate(fine.getDueDate())
                .status(fine.getStatus())
                .notes(fine.getNotes())
                .createdAt(fine.getCreatedAt())
                .updatedAt(fine.getUpdatedAt())
                .isFullyPaid(fine.isFullyPaid())
                .isOverdue(fine.isOverdue())
                .remainingAmount(fine.getRemainingAmount())
                .build();
    }

    public static Fine toEntity(FineDto dto) {
        if (dto == null) return null;
        Fine fine = new Fine();
        fine.setId(dto.getId());
        // Note: Loan should be set by the service layer with proper entity from repository
        fine.setAmount(dto.getAmount());
        fine.setPaidAmount(dto.getPaidAmount());
        fine.setDaysOverdue(dto.getDaysOverdue());
        fine.setFineDate(dto.getFineDate());
        fine.setDueDate(dto.getDueDate());
        fine.setStatus(dto.getStatus());
        fine.setNotes(dto.getNotes());
        return fine;
    }
} 