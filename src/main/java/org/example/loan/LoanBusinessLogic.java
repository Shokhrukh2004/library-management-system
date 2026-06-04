package org.example.loan;

import org.example.book.Book;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.Member;
import org.example.member.repository.MemberRepository;
import org.springframework.stereotype.Component;

@Component
public class LoanBusinessLogic {
    private final LoanRepository loanRepo;

    public LoanBusinessLogic(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }

    private void checkHasOverDue(int memberId){
        boolean hasOverdue = loanRepo.findByMemberId(memberId)
                .stream()
                .anyMatch(loan -> loan.getStatus() == Status.OVERDUE);

        if (hasOverdue){
            throw new ConflictException("Current loan is already overdue, please return first!");
        }
    }

    private void checkHasBorrowedSameBook(int memberId, int bookId){
        if(loanRepo.findActiveByMemberAndBook(memberId, bookId).isPresent()){
            throw new ConflictException("You already borrowed same book, please return first!");
        }
    }

    private void checkBookAvailability(Book book){
        if(book.getAvailableCopies() < 1) {
            throw new ConflictException("Book has no available copies, please try later!");
        }
    }

    private void checkIsBookActive(Book book){
        if(!book.isActive()){
            throw new ConflictException("Book is not active!");
        }
    }

    private void checkIsMemberActive(Member member){
        if(!member.isActive()){
            throw new ConflictException("Member is not active!");
        }
    }

    public void checkCreateRequest(Member member, Book book){
        checkHasOverDue(member.getId());
        checkIsBookActive(book);
        checkIsMemberActive(member);
        checkBookAvailability(book);
        checkHasBorrowedSameBook(member.getId(), book.getId());
    }


    public void checkReturned(Loan loan){
        if(loan.getStatus().equals(Status.RETURNED)){
            throw new ConflictException("Loan is already returned!");
        }
    }

}
