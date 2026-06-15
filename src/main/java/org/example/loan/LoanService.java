package org.example.loan;

import org.example.book.Book;
import org.example.book.repository.BookRepository;
import org.example.exception.NotFoundException;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.dto.LoanResponse;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.Member;
import org.example.member.repository.MemberRepository;
import org.example.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
public class LoanService {

    private final static Logger log = LoggerFactory.getLogger(LoanService.class);

    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    private final LoanRepository loanRepo;
    private final LoanConflictLogic loanLogic;

    public LoanService(BookRepository bookRepo,
                       MemberRepository memberRepo,
                       LoanRepository repo,
                       LoanConflictLogic loanLogic) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
        this.loanRepo = repo;
        this.loanLogic = loanLogic;
    }

    @Transactional
    public void save(LoanCreateRequest loanRequest) {
        log.info("Creating loan - memberId: {}, bookId: {}", loanRequest.getMemberId(), loanRequest.getBookId());

        Book book = getBookIfExist(loanRequest.getBookId());
        Member member = getMemberIfExist(loanRequest.getMemberId());

        loanLogic.checkCreateRequest(member, book);
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        bookRepo.save(book);
        loanRepo.save(LoanParser.toLoanFromCreateRequest(member, book));
        log.info("Loan created successfully - bookId: {}, memberId: {}", loanRequest.getBookId(), loanRequest.getMemberId());
    }

    public LoanResponse findById(int id){
        Validator.validateInt(id, "Id");
        Loan loan = getLoanIfExist(id);

        return LoanParser.toLoanResponseFromLoan(loan);
    }

    public List<LoanResponse> findByMemberId(int memberId){
        Validator.validateInt(memberId, "Member Id");
        List<Loan> loans = loanRepo.findByMember_Id(memberId);

        isEmptyCheck(loans, "with member Id: " + memberId);

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public List<LoanResponse> findByBookId(int bookId){
        Validator.validateInt(bookId, "Book id");
        List<Loan> loans = loanRepo.findByBook_Id(bookId);

        isEmptyCheck(loans, "with book Id: " + bookId);

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public LoanResponse findActiveByMemberAndBook(int memberId, int bookId){
        Validator.validateInt(memberId, "Member id");
        Validator.validateInt(bookId, "Book id");

        Loan loan = loanRepo.findByMember_IdAndBook_IdAndStatus(memberId, bookId, Status.ACTIVE)
                .orElseThrow(() ->
                        new NotFoundException("Loan not found with member id " + memberId + " and book id " + bookId));

        return LoanParser.toLoanResponseFromLoan(loan);
    }

    public List<LoanResponse> findAll(){
        List<Loan> loans = loanRepo.findAll();

        isEmptyCheck(loans, "");

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public List<LoanResponse> findOverdue(){
        List<Loan> loans = loanRepo.findByStatus(Status.OVERDUE);

        isEmptyCheck(loans, "with status: OVERDUE");

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public List<LoanResponse> findActive(){
        List<Loan> loans = loanRepo.findByStatus(Status.ACTIVE);

        isEmptyCheck(loans, "with status: ACTIVE");

        return loans
                .stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    public List<LoanResponse> findReturned(){
        List<Loan> loans = loanRepo.findByStatus(Status.RETURNED);

        isEmptyCheck(loans, "with status: RETURNED");

        return loans.stream()
                .map(LoanParser::toLoanResponseFromLoan)
                .toList();
    }

    @Transactional
    public void returnBook(int loanId){
        log.info("Returning loan - loanId: {}", loanId);
        Validator.validateInt(loanId, "Loan id");

        Loan loan = getLoanIfExist(loanId);
        Book book = loan.getBook();

        loanLogic.checkReturned(loan);

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        loan.setStatus(Status.RETURNED);
        loan.setReturnDate(LocalDate.now());

        bookRepo.save(book);
        loanRepo.save(loan);

        log.info("Returned loan successfully - loanId: {}", loanId);
    }


    private Book getBookIfExist(int bookId){
        return bookRepo.findById(bookId).orElseThrow(() -> {
            log.warn("Book not found with id {}", bookId);
            return new NotFoundException("Book not found with id: " + bookId);
        });

    }

    private Member getMemberIfExist(int memberId){
        return memberRepo.findById(memberId).orElseThrow(() ->{
            log.warn("Member not found with id: {}", memberId);
            return new NotFoundException("Member not found with id: " + memberId);
        });

    }

    private Loan getLoanIfExist(int loanId){
        return loanRepo.findById(loanId).orElseThrow(() -> {
            log.warn("Loan not found with loanId: {}", loanId);
            return new NotFoundException("Loan not found with loanId: " + loanId);
        });
    }

    private <T> void isEmptyCheck(List<T> items, String field){
        if(items.isEmpty()){
            throw new NotFoundException("Loan Not Found " + field);
        }
    }
}
