package org.example.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@ToString
public class MemberResponse {
    private final int id;
    private final String name;
    private final String email;
    private final LocalDateTime registerDate;
}
