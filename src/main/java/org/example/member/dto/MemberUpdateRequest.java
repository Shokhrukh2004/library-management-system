package org.example.member.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberUpdateRequest {

    @Positive
    private final int id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name should be minimum 3 characters")
    private final String name;

    @NotBlank(message = "Email is required")
    @Size(min = 3, message = "Email should be minimum 3 characters")
    private final String email;
}
