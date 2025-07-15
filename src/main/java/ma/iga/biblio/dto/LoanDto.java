package ma.iga.biblio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ma.iga.biblio.entity.Loan;
import ma.iga.biblio.entity.LoanStatus;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Loan data transfer object")
public class LoanDto {
    @Schema(description = "Unique identifier of the loan")
    private Long id;

    @Schema(description = "User information")
    private UserDto user;

    @Schema(description = "Book information")
    private BookDto book;

    @Schema(description = "Date when the book was loaned")
    private LocalDateTime loanDate;

    @Schema(description = "Due date for returning the book")
    private LocalDateTime dueDate;

    @Schema(description = "Actual return date (null if not returned)")
    private LocalDateTime returnDate;

    @Schema(description = "Status of the loan")
    private LoanStatus status;

    @Schema(description = "Additional notes")
    private String notes;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Whether the loan is overdue")
    private Boolean isOverdue;

    @Schema(description = "Number of days overdue")
    private Long daysOverdue;

    public static LoanDto fromEntity(Loan loan) {
        if (loan == null) return null;
        return LoanDto.builder()
                .id(loan.getId())
                .user(loan.getUser() != null ? UserDto.builder()
                    .id(loan.getUser().getId())
                    .username(loan.getUser().getUsername())
                    .email(loan.getUser().getEmail())
                    .firstName(loan.getUser().getFirstName())
                    .lastName(loan.getUser().getLastName())
                    .role(loan.getUser().getRole())
                    .status(loan.getUser().getStatus())
                    .build() : null)
                .book(loan.getBook() != null ? BookDto.builder()
                    .id(loan.getBook().getId())
                    .title(loan.getBook().getTitle())
                    .isbn13(loan.getBook().getIsbn13())
                    .isbn10(loan.getBook().getIsbn10())
                    .build() : null)
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .notes(loan.getNotes())
                .createdAt(loan.getCreatedAt())
                .updatedAt(loan.getUpdatedAt())
                .isOverdue(loan.isOverdue())
                .daysOverdue(loan.getDaysOverdue())
                .build();
    }

    public static Loan toEntity(LoanDto dto) {
        if (dto == null) return null;
        Loan loan = new Loan();
        loan.setId(dto.getId());
        // Note: User and Book should be set by the service layer with proper entities from repository
        loan.setLoanDate(dto.getLoanDate());
        loan.setDueDate(dto.getDueDate());
        loan.setReturnDate(dto.getReturnDate());
        loan.setStatus(dto.getStatus());
        loan.setNotes(dto.getNotes());
        return loan;
    }
} 