package org.example.validation;

import org.example.exception.ValidationException;

public class Validator {

    public static void validateString(String value, String fieldName){
        if(value == null || value.isBlank() || value.length() < 3){
            throw new ValidationException(fieldName + " should have at least 3 characters.");
        }
    }

    public static void validatePositiveInt(int value, String fieldName){
        if(value < 1){
            throw new ValidationException(fieldName + " should not be negative.");
        }
    }

    public static void validateNotNull(Object value, String fieldName){
        if(value == null){
            throw new ValidationException(fieldName + " should not be null.");
        }
    }

    public static void validateIntRange(int start, int end, String fieldName){
        if(start > end || start <= 0) {
            throw new ValidationException(fieldName + " should be > 0 and <=  " + end);
        }
    }
}
