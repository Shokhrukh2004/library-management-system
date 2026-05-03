package org.example.loan;

import org.example.book.Book;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.dto.LoanResponse;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.Member;
import org.example.member.repository.MemberRepository;
import org.example.validation.LoanValidator;
import org.example.validation.Validator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    private final LoanRepository loanRepo;

    public LoanService(BookRepository bookRepo,
                       MemberRepository memberRepo,
                       LoanRepository repo) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
        this.loanRepo = repo;
    }

    public void save(LoanCreateRequest loan){
        validateBorrow(loan);
        reduceAvailableCopies(loan.getBookId());
        loanRepo.save(LoanParser.toLoanFromCreateRequest(loan));
    }

    public LoanResponse findById(int id){
        Validator.validatePositiveInt(id, "Loan ID");

        return getJoinedTable(id);
    }

    public List<LoanResponse> findByMemberId(int memberId){
        Validator.validatePositiveInt(memberId, "Member ID");
        List<Loan> loans = loanRepo.findByMemberId(memberId);
        if(loans.isEmpty()){
            throw new NotFoundException("Loan Not Found with member ID: " + memberId);
        }

        return loans
                .stream()
                .map(loan -> getJoinedTable(loan.getId()))
                .toList();
    }

    public List<LoanResponse> findByBookId(int bookId){
        Validator.validatePositiveInt(bookId, "Book ID");

        List<Loan> loans = loanRepo.findByBookId(bookId);
        if(loans.isEmpty()){
            throw new NotFoundException("Loan Not Found with book ID: " + bookId);
        }

        return loans
                .stream()
                .map(loan -> getJoinedTable(loan.getId()))
                .toList();
    }

    public LoanResponse findActiveByMemberAndBook(int memberId, int bookId){
        Validator.validatePositiveInt(memberId, "Member ID");
        Validator.validatePositiveInt(bookId, "Book ID");

        Loan loan = loanRepo.findActiveByMemberAndBook(memberId, bookId)
                .orElseThrow(() ->
                        new NotFoundException("Loan not found with member id " + memberId + " and book id " + bookId));

        return getJoinedTable(loan.getId());
    }

    public List<LoanResponse> findAll(){
        List<Loan> loans = loanRepo.findAll();
        if(loans.isEmpty()){
            throw new NotFoundException("Loan Not Found");
        }

        return loans
                .stream()
                .map(loan -> getJoinedTable(loan.getId()))
                .toList();
    }

    public List<LoanResponse> findOverdue(){
        List<Loan> loans = loanRepo.findOverdue();
        if(loans.isEmpty()){
            throw new NotFoundException("Overdue Loans Not Found");
        }

        return loans
                .stream()
                .map(loan -> getJoinedTable(loan.getId()))
                .toList();
    }

    public List<LoanResponse> findActive(){
        List<Loan> loans = loanRepo.findActive();
        if(loans.isEmpty()){
            throw new NotFoundException("Active Loans Not Found");
        }

        return loans
                .stream()
                .map(loan -> getJoinedTable(loan.getId()))
                .toList();
    }

    public void returnBook(int loanId){
        Validator.validatePositiveInt(loanId, "Loan ID");

        Loan loan = getLoan(loanId);
        validateReturned(loan);

        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(LocalDate.now());
        increaseAvailableCopies(loan.getBookId());
        loanRepo.update(loan);
    }

    private void checkHasOverDue(int memberId){
        for(Loan loan1 : loanRepo.findByMemberId(memberId)){
            if(loan1.getStatus().equals(Status.OVERDUE)){
                throw new ConflictException("Current loan is already overdue, please return first!");
            }
        }
    }

    private void checkHasBorrowedSameBook(int memberId, int bookId){
        if(loanRepo.findActiveByMemberAndBook(memberId, bookId).isPresent()){
            throw new ConflictException("You already borrowed same book, please return first!");
        }
    }

    private void checkBookAvailability(int bookId){
        Book book = bookRepo.findById(bookId).get();
        if(book.getAvailableCopies() < 1) {
            throw new ConflictException("Book has no available copies, please try later!");
        }
    }

    private void validateBorrow(LoanCreateRequest loan){
        LoanValidator.validateLoanCreateRequest(loan);
        getBook(loan.getBookId());
        getMember(loan.getMemberId());
        checkHasBorrowedSameBook(loan.getBookId(), loan.getMemberId());
        checkHasOverDue(loan.getMemberId());
        checkBookAvailability(loan.getBookId());
    }

    private void validateReturned(Loan loan){
        if(loan.getStatus().equals(Status.RETURNED)){
            throw new ConflictException("Loan is already returned!");
        }
    }

    private Book getBook(int bookId){
        return bookRepo.findById(bookId).orElseThrow(() ->
                new NotFoundException("Book not found with id: " + bookId));
    }

    private Member getMember(int memberId){
        return memberRepo.findById(memberId).orElseThrow(() ->
                new NotFoundException("Member not found with id: "+ memberId));
    }

    private Loan getLoan(int loanId){
        return loanRepo.findById(loanId).orElseThrow(
                () -> new NotFoundException("Loan not found with id: " + loanId)
        );
    }

    private LoanResponse getJoinedTable(int loanId){
        Loan loan = getLoan(loanId);
        Book book = getBook(loan.getBookId());
        Member member = getMember(loan.getMemberId());

        return LoanParser.toLoanResponseFromLoan(loan, member, book);
    }

    private void reduceAvailableCopies(int bookId){
        Book book = getBook(bookId);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepo.update(book);
    }

    private void increaseAvailableCopies(int bookId){
        Book book = getBook(bookId);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepo.update(book);
    }
}
