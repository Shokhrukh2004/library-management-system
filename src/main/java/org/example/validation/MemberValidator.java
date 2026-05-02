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

    public static void validateEmail(String value){
        Validator.validateString(value, "Email");
        if(!value.contains("@") || !value.contains(".")){
            throw new ValidationException("email should be a valid email address.");
        }
    }


}
