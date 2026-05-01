package org.example.validation;

import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberUpdateRequest;

public class MemberValidator {

    public static void validateCreateRequest(MemberCreateRequest member){
        Validator.validateString(member.getName(), "Name");
        Validator.validateString(member.getEmail(), "Email");
    }

    public static void validateUpdateRequest(MemberUpdateRequest member){
        Validator.validatePositiveInt(member.getId(), "Id");
        Validator.validateString(member.getName(), "Name");
        Validator.validateString(member.getEmail(), "Email");
    }
}
