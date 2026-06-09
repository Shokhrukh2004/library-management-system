package org.example.book;

import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.loan.Loan;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class BookServiceActivateDeactivateTest {
    @Mock
    BookRepository bookRepo;

    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    BookService bookService;

    //       Book activate method test cases
    @Test
    void activate_validRequest_bookIsActivated(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc124", 20, 20, false);
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        bookService.activate(1);

        verify(bookRepo).activate(book.getId());
    }

    @Test
    void activate_bookDoesNotExist_throwsNotFoundException(){
        when(bookRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.activate(1));

        verify(bookRepo, never()).activate(anyInt());
    }

    @Test
    void activate_alreadyActiveBook_throwsConflictException(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc124", 20, 20, true);
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        assertThrows(ConflictException.class, () -> bookService.activate(1));

        verify(bookRepo, never()).activate(anyInt());
    }

    @Test
    void activate_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class, () -> bookService.activate(-1));

        verify(bookRepo, never()).activate(anyInt());
    }


    //       Book deactivate method test cases
    @Test
    void deactivate_validRequest_bookIsDeactivated(){
        Book book = new Book(1,  "Clean Code", "John Doe", "abc124", 20, 20, true);

        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(loanRepo.findByBookId(1)).thenReturn(List.of());

        bookService.deactivate(1);

        verify(bookRepo).deactivate(book.getId());
    }

    @Test
    void deactivate_bookDoesNotExist_throwsNotFoundException(){
        when(bookRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.deactivate(1));

        verify(bookRepo, never()).deactivate(anyInt());
    }

    @Test
    void deactivate_alreadyInactiveBook_throwsConflictException(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc124", 20, 20, false);
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        assertThrows(ConflictException.class, () -> bookService.deactivate(1));

        verify(bookRepo, never()).deactivate(anyInt());
    }

    @Test
    void deactivate_loanedBook_throwsConflictException(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc124", 20, 20, true);
        Loan loan = new Loan(1, 1, 1, null, null, null, Status.ACTIVE);

        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        when(loanRepo.findByBookId(1)).thenReturn(List.of(loan));

        assertThrows(ConflictException.class, () -> bookService.deactivate(1));

        verify(bookRepo, never()).deactivate(anyInt());
    }

    @Test
    void deactivate_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class, () -> bookService.deactivate(-1));

        verify(bookRepo, never()).deactivate(anyInt());
    }

}
