package ma.iga.biblio.entity;

public enum LoanStatus {
    ACTIVE("Book is currently on loan"),
    RETURNED("Book has been returned"),
    OVERDUE("Book is past due date"),
    LOST("Book has been reported as lost"),
    DAMAGED("Book has been returned damaged");

    private final String description;

    LoanStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 