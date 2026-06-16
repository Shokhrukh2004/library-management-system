package org.example.member;

import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberUpdateRequest;

import java.time.LocalDate;

public class MemberUtil {
    public static MemberCreateRequest getCreateRequest(boolean isValidEmail){
        return new MemberCreateRequest(
                "John Doe",
                isValidEmail ? "john@mail.ru" : "john.ru"
        );
    }

    public static MemberUpdateRequest getUpdateRequest(boolean isValidEmail){
        return new MemberUpdateRequest(
                1,
                "Johny Depp",
                isValidEmail ? "johny@mail.com" : "john.ru"
        );
    }

    public static Member getMember(boolean isActive){
        return new Member(
                1,
                "John Doe",
                "john@mail.ru",
                LocalDate.now(),
                isActive
        );
    }
}
