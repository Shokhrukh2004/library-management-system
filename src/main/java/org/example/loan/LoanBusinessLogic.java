package org.example.loan;

import org.example.book.Book;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class LoanBusinessLogic {
    private final LoanRepository loanRepo;
    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;

    public LoanBusinessLogic(LoanRepository loanRepo,
                             BookRepository bookRepo,
                             MemberRepository memberRepo) {
        this.loanRepo = loanRepo;
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
    }

    public void checkHasOverDue(int memberId){
        boolean hasOverdue = loanRepo.findByMemberId(memberId)
                .stream()
                .anyMatch(loan -> loan.getStatus() == Status.OVERDUE);

        if (hasOverdue){
            throw new ConflictException("Current loan is already overdue, please return first!");
        }
    }

    public void checkHasBorrowedSameBook(int memberId, int bookId){
        if(loanRepo.findActiveByMemberAndBook(memberId, bookId).isPresent()){
            throw new ConflictException("You already borrowed same book, please return first!");
        }
    }

    public void checkBookAvailability(Book book){
        if(book.getAvailableCopies() < 1) {
            throw new ConflictException("Book has no available copies, please try later!");
        }
    }


    public void checkReturned(Loan loan){
        if(loan.getStatus().equals(Status.RETURNED)){
            throw new ConflictException("Loan is already returned!");
        }
    }
}
