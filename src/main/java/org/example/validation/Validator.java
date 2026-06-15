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

    public static void validateEmail(String value) {
        int atIndex = value.indexOf("@");

        if (atIndex <= 0 || atIndex == value.length() - 1) {
            throw new ValidationException("Email must have content before and after @.");
        }

        String domain = value.substring(atIndex + 1);

        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) {
            throw new ValidationException("Email domain must be valid gmail.com");
        }
    }
}
