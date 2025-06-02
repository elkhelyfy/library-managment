package ma.iga.biblio.entity;

public enum PaymentType {
    CASH("Cash payment"),
    CREDIT_CARD("Credit card payment"),
    DEBIT_CARD("Debit card payment"),
    BANK_TRANSFER("Bank transfer"),
    MOBILE_PAYMENT("Mobile payment"),
    CHECK("Check payment");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 