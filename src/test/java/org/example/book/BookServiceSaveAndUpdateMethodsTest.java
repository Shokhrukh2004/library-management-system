package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookUpdateRequest;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.loan.Loan;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceSaveAndUpdateMethodsTest {

    @Mock
    private BookRepository bookRepo;

    @Mock
    private LoanRepository loanRepo;

    @InjectMocks
    private BookService bookService;


    //       Book save method test cases
    @Test
    void save_validRequest_bookIsSaved(){
        BookCreateRequest request =
                new BookCreateRequest("Clean Code", "Robert Martin", "abc123", 10);

        when(bookRepo.findByIsbn("abc123")).thenReturn(Optional.empty());

        bookService.save(request);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

        verify(bookRepo).save(captor.capture());

        Book savedBook = captor.getValue();

        assertAll(
                () -> assertEquals("Clean Code", savedBook.getTitle()),
                () -> assertEquals("Robert Martin", savedBook.getAuthor()),
                () -> assertEquals("abc123", savedBook.getIsbn()),
                () -> assertEquals(10, savedBook.getTotalCopies())
        );
    }

    @Test
    void save_invalidIsbn_throwsConflictException(){
        BookCreateRequest request = new BookCreateRequest("Clean Code", "Robert Martin", "abc123", 10);

        when(bookRepo.findByIsbn(anyString())).thenReturn(Optional.of(new Book()));

        assertThrows(ConflictException.class, () -> bookService.save(request));

        verify(bookRepo).findByIsbn("abc123");
        verifyNoMoreInteractions(bookRepo);
    }

    @Test
    void save_invalidInput_throwsValidationException(){
        BookCreateRequest request = new BookCreateRequest("", "", "abc123", 10);

        assertThrows(ValidationException.class, () -> bookService.save(request));

        verifyNoInteractions(bookRepo);
    }


    //      Book update method test cases
    @Test
    void update_validRequest_bookIsUpdated(){
        BookUpdateRequest request = new BookUpdateRequest(1, 20, 20);
        Book book = new Book(1, "Clean Code", "John Doe", "abc124", 20, 20, true);

        when(bookRepo.findById(1)).thenReturn(Optional.of(book));
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

        bookService.update(request);

        verify(bookRepo).update(captor.capture());

        Book updatedBook = captor.getValue();
        assertAll(
                () -> assertEquals(request.getId(), updatedBook.getId()),
                () -> assertEquals(request.getTotalCopies(), updatedBook.getTotalCopies()),
                () -> assertEquals(request.getAvailableCopies(), updatedBook.getAvailableCopies())
        );
    }

    @Test
    void update_inactiveBook_throwsConflictException(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc124", 20, 20, false);

        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        assertThrows(ConflictException.class, () -> bookService.update(new BookUpdateRequest(1, 20, 20)));

        verify(bookRepo, never()).update(any());
    }

    @Test
    void update_bookDoesNotExist_throwsNotFoundException(){
        BookUpdateRequest request = new BookUpdateRequest(1, 20, 20);

        when(bookRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.update(request));

        verify(bookRepo, never()).update(any());
    }

    @Test
    void update_invalidInput_throwsValidationException(){
        BookUpdateRequest request = new BookUpdateRequest(1, -1, 22);

        assertThrows(ValidationException.class, () -> bookService.update(request));

        verify(bookRepo, never()).update(any());
    }


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
