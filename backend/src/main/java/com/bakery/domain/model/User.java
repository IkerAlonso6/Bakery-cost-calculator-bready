package com.bakery.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Usuario de la aplicación (dueño de su propia panadería: insumos, recetas,
 * productos, costos y configuración). La foto de perfil se maneja a nivel de
 * persistencia (bytes), no en el dominio puro.
 */
public class User {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final Integer id;
    private final String email;
    private final String passwordHash;
    private String displayName;

    public User(String email, String passwordHash, String displayName) {
        this(null, email, passwordHash, displayName);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public User(Integer id, String email, String passwordHash, String displayName) {
        validateEmail(email);
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("User password hash must not be blank");
        }
        validateDisplayName(displayName);
        this.id = id;
        this.email = email.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.displayName = displayName.trim();
    }

    public void changeDisplayName(String newDisplayName) {
        validateDisplayName(newDisplayName);
        this.displayName = newDisplayName.trim();
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("User email is invalid");
        }
    }

    private static void validateDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("User display name must not be blank");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', displayName='" + displayName + "'}";
    }
}
