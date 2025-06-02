package ma.iga.biblio.entity;

public enum FineStatus {
    PENDING("Fine has been issued but not yet paid"),
    PAID("Fine has been fully paid"),
    PARTIALLY_PAID("Fine has been partially paid"),
    WAIVED("Fine has been waived by administrator"),
    CANCELLED("Fine has been cancelled"),
    OVERDUE("Fine payment is past due date");

    private final String description;

    FineStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 