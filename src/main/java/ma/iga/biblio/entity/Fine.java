package ma.iga.biblio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "fines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @NotNull
    @Min(0)
    @Column(name = "amount", nullable = false)
    private Double amount;

    @NotNull
    @Min(0)
    @Column(name = "paid_amount", nullable = false)
    private Double paidAmount = 0.0;

    @NotNull
    @Column(name = "days_overdue", nullable = false)
    private Integer daysOverdue;

    @NotNull
    @Column(name = "fine_date", nullable = false)
    private LocalDateTime fineDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FineStatus status = FineStatus.PENDING;

    @OneToMany(mappedBy = "fine", cascade = CascadeType.ALL)
    private Set<Payment> payments = new HashSet<>();

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (fineDate == null) {
            fineDate = LocalDateTime.now();
        }
        // Set default due date to 30 days from fine date
        if (dueDate == null) {
            dueDate = fineDate.plusDays(30);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to check if fine is fully paid
    public boolean isFullyPaid() {
        return paidAmount >= amount;
    }

    // Helper method to check if fine is overdue
    public boolean isOverdue() {
        return status == FineStatus.PENDING && LocalDateTime.now().isAfter(dueDate);
    }

    // Helper method to get remaining amount
    public Double getRemainingAmount() {
        return Math.max(0, amount - paidAmount);
    }
} 