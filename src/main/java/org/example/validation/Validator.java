package org.example.validation;

import org.example.exception.ValidationException;
import org.springframework.web.bind.annotation.PathVariable;

public class Validator {

    public static void validateInt(int value, String field){
        if(value < 1) {
            throw new ValidationException(field + " must be positive");
        }
    }

    public static void validateStr(String value, String field){
        if(value == null || value.isBlank()) {
            throw new ValidationException(field + " is required");
        }
        else if(value.length() < 3) {
            throw new ValidationException(field + " must have at least 3 characters");
        }
    }
}
