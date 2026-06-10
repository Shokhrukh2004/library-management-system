package org.example.validation;

import org.example.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberValidatorTest {

    @Test
    void validateEmail_validEmail_noExceptionThrown() {
        assertDoesNotThrow(() -> MemberValidator.validateEmail("john@mail.com"));
    }

    @Test
    void validateEmail_noAtSign_throwsValidationException() {
        assertThrows(ValidationException.class, () -> MemberValidator.validateEmail("johnmail.com"));
    }

    @Test
    void validateEmail_atSignAtStart_throwsValidationException() {
        assertThrows(ValidationException.class, () -> MemberValidator.validateEmail("@mail.com"));
    }

    @Test
    void validateEmail_nothingAfterAt_throwsValidationException() {
        assertThrows(ValidationException.class, () -> MemberValidator.validateEmail("john@"));
    }

    @Test
    void validateEmail_noDotInDomain_throwsValidationException() {
        assertThrows(ValidationException.class, () -> MemberValidator.validateEmail("john@mailcom"));
    }

    @Test
    void validateEmail_domainStartsWithDot_throwsValidationException() {
        assertThrows(ValidationException.class, () -> MemberValidator.validateEmail("john@.mail.com"));
    }

    @Test
    void validateEmail_domainEndsWithDot_throwsValidationException() {
        assertThrows(ValidationException.class, () -> MemberValidator.validateEmail("john@mail."));
    }
}
