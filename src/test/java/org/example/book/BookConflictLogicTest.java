package org.example.book;

import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookConflictLogicTest {

    @Mock
    BookRepository bookRepo;

    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    BookConflictLogic logic;

    //      isbnDuplicateCheck method test cases
    @Test
    void isbnDuplicateCheck_noDuplicate_doesNotThrow(){
        when(bookRepo.findByIsbn(any(String.class)))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(
                () -> logic.isbnDuplicateCheck("isbn123"));
    }

    @Test
    void isbnDuplicateCheck_isbnExists_throwsConflictException(){
        when(bookRepo.findByIsbn(any(String.class)))
                .thenReturn(Optional.of(new Book()));

        assertThrows(ConflictException.class,
                () -> logic.isbnDuplicateCheck("isbn123"));
    }


    //      isBookLoanedCheck method test cases
    @Test
    void isBookLoanedCheck_noLoan_doesNotThrow(){
        when(loanRepo
                .existsByBook_IdAndStatus(any(Integer.class),
                        any(Status.class)))
                .thenReturn(false);

        assertDoesNotThrow(
                () -> logic.isBookLoanedCheck(1));
    }

    @Test
    void isBookLoanedCheck_loanExists_throwsConflictException(){
        when(loanRepo
                .existsByBook_IdAndStatus(any(Integer.class), any(Status.class)))
                .thenReturn(true);

        assertThrows(ConflictException.class,
                () -> logic.isBookLoanedCheck(1));
    }


    //      isBookActiveCheck method test cases
    @Test
    void isBookActiveCheck_bookActive_doesNotThrow(){
        Book book = BookUtil.getBook(true);

        assertDoesNotThrow(
                () -> logic.isBookActiveCheck(book));
    }

    @Test
    void isBookActiveCheck_bookNotActive_throwsConflictException(){
        Book book = BookUtil.getBook(false);

        assertThrows(ConflictException.class,
                () -> logic.isBookActiveCheck(book));
    }


    //      isBookNotActiveCheck method test cases
    @Test
    void isBookNotActiveCheck_bookNotActive_doesNotThrow(){
        Book book = BookUtil.getBook(false);

        assertDoesNotThrow(
                () -> logic.isBookNotActiveCheck(book));
    }

    @Test
    void isBookNotActiveCheck_bookActive_throwsConflictException(){
        Book book = BookUtil.getBook(true);

        assertThrows(ConflictException.class,
                () -> logic.isBookNotActiveCheck(book));
    }


    //      validateTotalCopies method test cases
    @Test
    void validateTotalCopies_validCopies_doesNotThrow(){
        assertDoesNotThrow(() -> logic.validateCopies(20, 20));
    }

    @Test
    void validateTotalCopies_invalidCopies_throwsConflictException(){
        assertThrows(ConflictException.class,
                () -> logic.validateCopies(20, 25));
    }
}
