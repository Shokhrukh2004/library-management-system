package org.example.book;

import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.ValidationException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BookConflictLogic {

    Logger log = LoggerFactory.getLogger(BookConflictLogic.class);

    private final BookRepository bookRepo;
    private final LoanRepository loanRepo;

    public BookConflictLogic(BookRepository bookRepo, LoanRepository loanRepo) {
        this.bookRepo = bookRepo;
        this.loanRepo = loanRepo;
    }


    public void isbnDuplicateCheck(String isbn){
        if(bookRepo.findByIsbn(isbn).isPresent()){
            log.warn("Book with ISBN {} already exists", isbn);
            throw new ConflictException("Book with ISBN " + isbn + " already exists");
        }
    }

    public void isBookLoanedCheck(int bookId) {
        boolean isActive = loanRepo.existsByBook_IdAndStatus(bookId, Status.ACTIVE);
        boolean isOverdue = loanRepo.existsByBook_IdAndStatus(bookId, Status.OVERDUE);

        if (isActive || isOverdue) {
            log.warn("Book is already loaned - id: {}", bookId);
            throw new ConflictException("Book with ID " + bookId + " is loaned");
        }
    }

    public void isBookActiveCheck(Book book) {
        if(!book.isActive()){
            log.warn("Book is not active - id: {}", book.getId());
            throw new ConflictException("Book is not active");
        }
    }

    public void isBookNotActiveCheck(Book book){
        if(book.isActive()){
            log.warn("Book is active already - id: {}", book.getId());
            throw new ConflictException("Book is active already");
        }
    }

    public void validateCopies(int totalCopies, int availableCopies){
        if(totalCopies < availableCopies){
            throw new ConflictException("Total Copies can't be less than Available Copies");
        }
    }
}
