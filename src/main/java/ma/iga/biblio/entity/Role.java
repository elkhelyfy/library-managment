package ma.iga.biblio.entity;

public enum Role {
    ROLE_ADMIN("Administrator with full system access"),
    ROLE_LIBRARIAN("Library staff with book management access"),
    ROLE_MEMBER("Regular library member"),
    ROLE_STUDENT("Student with extended borrowing privileges"),
    ROLE_FACULTY("Faculty member with special privileges");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthority() {
        return name();
    }
}
