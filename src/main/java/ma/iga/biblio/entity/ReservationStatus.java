package ma.iga.biblio.entity;

public enum ReservationStatus {
    PENDING("Waiting for librarian approval"),
    APPROVED("Reservation approved and ready for pickup"),
    REJECTED("Reservation request was rejected"),
    CANCELLED("Reservation was cancelled by user"),
    FULFILLED("Book has been picked up"),
    EXPIRED("Reservation expired without pickup"),
    ON_HOLD("Book is currently on hold for this user");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
