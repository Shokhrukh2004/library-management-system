package org.example.loan.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoanCreateRequest {
    @Positive
    private final int memberId;
    @Positive
    private final int bookId;
}
