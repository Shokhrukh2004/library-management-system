package org.example.loan;

import org.example.book.Book;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.Member;
import org.example.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoanBusinessLogic {

    private final static Logger log =  LoggerFactory.getLogger(LoanBusinessLogic.class);
    private final LoanRepository loanRepo;

    public LoanBusinessLogic(LoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }

    private void checkHasOverDue(int memberId){
        boolean hasOverdue = loanRepo.findByMember_Id(memberId)
                .stream()
                .anyMatch(loan -> loan.getStatus() == Status.OVERDUE);

        if (hasOverdue){
            log.warn("Member has overdue already - memberId: {}", memberId);
            throw new ConflictException("Member has overdue already, please return first!");
        }
    }

    private void checkHasBorrowedSameBook(int memberId, int bookId){
        if(loanRepo.findByMember_IdAndBook_IdAndStatus(memberId, bookId, Status.ACTIVE).isPresent()){
            log.warn("Member has already borrowed same book - memberId: {}, bookId: {}", memberId, bookId);
            throw new ConflictException("You already borrowed same book, please return first!");
        }
    }

    private void checkBookAvailability(Book book){
        if(book.getAvailableCopies() < 1) {
            log.warn("Book has no available copies - bookId: {}", book.getId());
            throw new ConflictException("Book has no available copies, please try later!");
        }
    }

    private void checkIsBookActive(Book book){
        if(!book.isActive()){
            log.warn("Book is not active - bookId: {}", book.getId());
            throw new ConflictException("Book is not active!");
        }
    }

    private void checkIsMemberActive(Member member){
        if(!member.isActive()){
            log.warn("Member is not active - memberId: {}", member.getId());
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
            log.warn("Loan has already been returned - loanId: {}", loan.getId());
            throw new ConflictException("Loan is already returned!");
        }
    }
}
