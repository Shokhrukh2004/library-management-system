package org.example.loan;

import org.example.book.Book;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.dto.LoanResponse;
import org.example.loan.enums.Status;
import org.example.member.Member;

import java.time.LocalDate;

public class LoanParser {

    public static Loan toLoanFromCreateRequest(LoanCreateRequest request, Member member, Book book) {
        return new Loan(
                0,
                member,
                book,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                null,
                Status.ACTIVE
        );
    }

    public static LoanResponse toLoanResponseFromLoan(Loan loan){
        return new LoanResponse(
                loan.getId(),
                loan.getMember().getId(),
                loan.getBook().getId(),
                loan.getBorrowDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }
}
