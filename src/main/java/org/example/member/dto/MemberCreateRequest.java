package org.example.member.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberCreateRequest {
    @NotBlank(message = "Name is required")
    @Min(value = 3, message = "Name should be minimum 3 characters")
    private final String name;

    @NotBlank(message = "Email is required")
    @Min(value = 3, message = "Name should be minimum 3 characters")
    private final String email;
}
