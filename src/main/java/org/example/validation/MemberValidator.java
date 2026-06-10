package org.example.validation;

import org.example.exception.ValidationException;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberUpdateRequest;

public class MemberValidator {

    public static void validateCreateRequest(MemberCreateRequest member){
        Validator.validateString(member.getName(), "Name");
        validateEmail(member.getEmail());
    }

    public static void validateUpdateRequest(MemberUpdateRequest member){
        Validator.validatePositiveInt(member.getId(), "Id");
        Validator.validateString(member.getName(), "Name");
        validateEmail(member.getEmail());
    }

    public static void validateEmail(String value) {
        Validator.validateString(value, "Email");

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
