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
        assertDoesNotThrow(() -> Validator.validateString("Value", "Field"));
    }

    @Test
    void validateString_nullValue_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateString(null, "Field"));
    }

    @Test
    void validateString_blankValue_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateString("", "Field"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "ba", "a", "c", "12"})
    void validateString_shortValue_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateString("ab", "Field"));
    }


    //      validatePositiveInt method test cases
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void validatePositiveInt_positiveInt_doesNotThrow(){
        assertDoesNotThrow(() -> Validator.validatePositiveInt(1, "Field"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, -10})
    void validatePositiveInt_negativeInt_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validatePositiveInt(-1, "Field"));
    }

    //      validateNotNull method test cases
    @Test
    void validateNotNull_nonNull_doesNotThrow(){
        assertDoesNotThrow(() -> Validator.validateNotNull(new Object(), "Field"));
    }

    @Test
    void validateNotNull_nullValue_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateNotNull(null, "Field"));
    }


    //      validateIntRange method test cases
    @Test
    void validateIntRange_startGreaterThanEnd_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateIntRange(10, 5, "Field"));
    }

    @Test
    void validateIntRange_startLessThanZero_throwsValidationException(){
        assertThrows(ValidationException.class, () -> Validator.validateIntRange(-5, 3, "Field"));
    }

    @Test
    void validateIntRange_validRange_doesNotThrow(){
        assertDoesNotThrow(() -> Validator.validateIntRange(1, 5, "Field"));
    }
}
