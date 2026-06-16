package org.example.validation;

import org.example.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    //      validateString method test cases
    @Test
    void validateString_validValue_doesNotThrow(){
        assertDoesNotThrow(() -> Validator.validateStr("Value", "Field"));
    }

    @Test
    void validateString_nullValue_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateStr(null, "Field"));
    }

    @Test
    void validateString_blankValue_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateStr("", "Field"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "ba", "a", "c", "12"})
    void validateString_shortValue_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateStr("ab", "Field"));
    }


    //      validatePositiveInt method test cases
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void validatePositiveInt_positiveInt_doesNotThrow(){
        assertDoesNotThrow(() -> Validator.validateInt(1, "Field"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, -10})
    void validatePositiveInt_negativeInt_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateInt(-1, "Field"));
    }


    //       validateEmail method test cases
    @Test
    void validateEmail_validEmail_noExceptionThrown() {
        assertDoesNotThrow(() -> Validator.validateEmail("john@mail.com"));
    }

    @Test
    void validateEmail_noAtSign_throwsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.validateEmail("johnmail.com"));
    }

    @Test
    void validateEmail_atSignAtStart_throwsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.validateEmail("@mail.com"));
    }

    @Test
    void validateEmail_nothingAfterAt_throwsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.validateEmail("john@"));
    }

    @Test
    void validateEmail_noDotInDomain_throwsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.validateEmail("john@mailcom"));
    }

    @Test
    void validateEmail_domainStartsWithDot_throwsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.validateEmail("john@.mail.com"));
    }

    @Test
    void validateEmail_domainEndsWithDot_throwsValidationException() {
        assertThrows(ValidationException.class,
                () -> Validator.validateEmail("john@mail."));
    }
}
