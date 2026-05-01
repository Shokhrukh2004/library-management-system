package org.example.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberCreateRequest {
    private final String name;
    private final String email;
}
