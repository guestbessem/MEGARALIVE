package org.example.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        User user = new User("john_doe", "john@example.com", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "There should be no validation errors for a valid user.");
    }

    @Test
    public void testInvalidEmail() {
        User user = new User("john_doe", "invalid-email", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email should be invalid.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")),
                "Validation error should be on the email field.");
    }

    @Test
    public void testEmptyUsername() {
        User user = new User("", "john@example.com", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Username should not be blank.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")),
                "Validation error should be on the username field.");
    }

    @Test
    public void testPasswordConstraints() {
        User user = new User("john_doe", "john@example.com", "");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Password should not be blank.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")),
                "Validation error should be on the password field.");
    }

    @Test
    public void testUserRoleAssociation() {
        User user = new User("john_doe", "john@example.com", "password123");
        Role role = new Role(ERole.Developpeur);
        user.getRoles().add(role);

        assertNotNull(user.getRoles(), "Roles should not be null.");
        assertTrue(user.getRoles().contains(role), "User should have the assigned role.");
    }
}
