package org.example.loan;

import org.example.book.Book;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.dto.LoanResponse;
import org.example.loan.enums.Status;
import org.example.member.Member;

import java.time.LocalDate;

public class LoanParser {

    public static Loan toLoanFromCreateRequest(LoanCreateRequest request){
        return new Loan(
                0,
                request.getMemberId(),
                request.getBookId(),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                null,
                Status.ACTIVE
        );
    }

    public static LoanResponse toLoanResponseFromLoan(Loan loan){
        return new LoanResponse(
                loan.getId(),
                loan.getMemberId(),
                loan.getBookId(),
                loan.getBorrowDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }
}
