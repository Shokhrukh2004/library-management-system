package org.example.member;

import org.example.book.Book;
import org.example.book.dto.BookResponse;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberResponse;

import java.time.LocalDate;

public class MemberParser {

    public static Member toMemberFromCreateRequest(MemberCreateRequest member){
        return new Member(
                0,
                member.getName(),
                member.getEmail(),
                LocalDate.now(),
                true
        );
    }

    public static MemberResponse toResponseFromMember(Member member){
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getRegisterDate()
        );
    }
}
