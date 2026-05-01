package org.example.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberUpdateRequest {
    private final int id;
    private final String name;
    private final String email;
}
