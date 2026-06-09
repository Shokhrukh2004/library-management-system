package org.example.loan;

import org.example.book.Book;
import org.example.book.repository.BookRepository;
import org.example.exception.NotFoundException;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.dto.LoanResponse;
import org.example.loan.repository.LoanRepository;
import org.example.member.Member;
import org.example.member.repository.MemberRepository;
import org.example.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class LoanService {

    private final static Logger log = LoggerFactory.getLogger(LoanService.class);

    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    private final LoanRepository loanRepo;
    private final LoanBusinessLogic loanLogic;

    public LoanService(BookRepository bookRepo,
                       MemberRepository memberRepo,
                       LoanRepository repo,
                       LoanBusinessLogic loanLogic) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
        this.loanRepo = repo;
        this.loanLogic = loanLogic;
    }

    @Transactional
    public void save(LoanCreateRequest loanRequest) {
        log.info("Creating loan - memberId: {}, bookId: {}", loanRequest.getMemberId(), loanRequest.getBookId());
        Validator.validatePositiveInt(loanRequest.getMemberId(), "memberId");
        Validator.validatePositiveInt(loanRequest.getBookId(), "bookId");


        Book book = getBook(loanRequest.getBookId());
        Member member = getMember(loanRequest.getMemberId());

        loanLogic.checkCreateRequest(member, book);
        bookRepo.decreaseAvailableCopies(book.getId());

        loanRepo.save(LoanParser.toLoanFromCreateRequest(loanRequest));
        log.info("Loan created successfully - bookId: {}, memberId: {}", loanRequest.getBookId(), loanRequest.getMemberId());
    }

    public LoanResponse findById(int id){
        Validator.validatePositiveInt(id, "Loan ID");
        Loan loan = loanRepo.findById(id).orElseThrow(() -> new NotFoundException("Book not found with id " + id));

        return LoanParser.toLoanResponseFromLoan(loan);
    }

    public List<LoanResponse> findByMemberId(int memberId){
        Validator.validatePositiveInt(memberId, "Member ID");
        List<Loan> loans = loanRepo.findByMemberId(memberId);
        if(loans.isEmpty()){
            throw new NotFoundException("Loan Not Found with member ID: " + memberId);
        }

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
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
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public LoanResponse findActiveByMemberAndBook(int memberId, int bookId){
        Validator.validatePositiveInt(memberId, "Member ID");
        Validator.validatePositiveInt(bookId, "Book ID");

        Loan loan = loanRepo.findActiveByMemberAndBook(memberId, bookId)
                .orElseThrow(() ->
                        new NotFoundException("Loan not found with member id " + memberId + " and book id " + bookId));

        return LoanParser.toLoanResponseFromLoan(loan);
    }

    public List<LoanResponse> findAll(){
        List<Loan> loans = loanRepo.findAll();
        if(loans.isEmpty()){
            throw new NotFoundException("Loan Not Found");
        }

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public List<LoanResponse> findOverdue(){
        List<Loan> loans = loanRepo.findOverdue();
        if(loans.isEmpty()){
            throw new NotFoundException("Overdue Loans Not Found");
        }

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public List<LoanResponse> findActive(){
        List<Loan> loans = loanRepo.findActive();
        if(loans.isEmpty()){
            throw new NotFoundException("Active Loans Not Found");
        }

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public List<LoanResponse> findReturned(){
        List<Loan> loans = loanRepo.findReturned();
        if(loans.isEmpty()){
            throw new NotFoundException("Returned Loans Not Found");
        }

        return loans.stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    @Transactional
    public void returnBook(int loanId){
        log.info("Returning loan - loanId: {}", loanId);
        Validator.validatePositiveInt(loanId, "Loan ID");

        Loan loan = getLoan(loanId);
        loanLogic.checkReturned(loan);

        bookRepo.increaseAvailableCopies(loan.getBookId());

        loanRepo.returnLoan(loanId);
        log.info("Returned loan successfully - loanId: {}", loanId);
    }


    private Book getBook(int bookId){
        return bookRepo.findById(bookId).orElseThrow(() -> {
            log.warn("Book not found with id {}", bookId);
            return new NotFoundException("Book not found with id: " + bookId);
        });

    }

    private Member getMember(int memberId){
        return memberRepo.findById(memberId).orElseThrow(() ->{
            log.warn("Member not found with id: {}", memberId);
            return new NotFoundException("Member not found with id: " + memberId);
        });

    }

    private Loan getLoan(int loanId){
        return loanRepo.findById(loanId).orElseThrow(() -> {
            log.warn("Loan not found with loanId: {}", loanId);
            return new NotFoundException("Loan not found with loanId: " + loanId);
        });
    }
}
