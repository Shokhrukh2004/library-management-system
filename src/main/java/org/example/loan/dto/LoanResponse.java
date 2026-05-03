package org.example.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.example.loan.enums.Status;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@ToString
public class LoanResponse {
    private final int loanId;
    private final int memberId;
    private final int bookId;
    private final String memberName;
    private final String bookName;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;
    private final Status status;
}
