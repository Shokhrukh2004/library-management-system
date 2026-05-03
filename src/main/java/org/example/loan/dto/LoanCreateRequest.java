package org.example.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoanCreateRequest {
    private final int memberId;
    private final int bookId;
}
