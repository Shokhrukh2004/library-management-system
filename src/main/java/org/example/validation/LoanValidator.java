package org.example.validation;

import org.example.loan.dto.LoanCreateRequest;

public class LoanValidator {

    public static void validateLoanCreateRequest(LoanCreateRequest loan){
        Validator.validatePositiveInt(loan.getBookId(), "Book ID");
        Validator.validatePositiveInt(loan.getMemberId(),  "Member ID");
    }

}
