package org.example.book;

import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceActivateDeactivateTest {

    @Mock
    BookRepository repo;

    @Mock
    BookConflictLogic logic;

    @InjectMocks
    BookService service;

    @Test
    void activate_validRequest_bookIsActivated() {
        Book book = getBook(false);

        when(repo.findById(1))
                .thenReturn(Optional.of(book));

        doNothing()
                .when(logic)
                .isBookNotActiveCheck(book);

        service.activate(book.getId());

        assertTrue(book.isActive());

        verify(repo).save(book);
    }

    @Test
    void activate_bookIsActiveAlready_throwsConflictException(){
        Book book = getBook(true);

        when(repo.findById(1))
                .thenReturn(Optional.of(book));

        doThrow(ConflictException.class)
                .when(logic).isBookNotActiveCheck(book);

        assertThrows(ConflictException.class,
                () -> service.activate(book.getId()));

        verify(repo, never()).save(book);
    }

    @Test
    void activate_bookDoesNotExist_throwsNotFoundException(){
        when(repo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.activate(1));

        verifyNoInteractions(logic);
        verify(repo, never()).save(any());
    }

    @Test
    void activate_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class,
                () -> service.activate(-1));

        verifyNoInteractions(logic);
        verifyNoInteractions(repo);
    }

    @Test
    void deactivate_validRequest_bookIsDeactivated() {
        Book book = getBook(true);

        when(repo.findById(1))
                .thenReturn(Optional.of(book));

        doNothing().when(logic)
                .isBookActiveCheck(book);

        doNothing()
                .when(logic)
                .isBookLoanedCheck(book.getId());

        service.deactivate(1);

        assertFalse(book.isActive());
        verify(repo).save(book);
    }

    @Test
    void deactivate_bookIsLoaned_throwsConflictException(){
        Book book = getBook(true);

        when(repo.findById(1))
                .thenReturn(Optional.of(book));

        doNothing()
                .when(logic)
                .isBookActiveCheck(book);

        doThrow(ConflictException.class)
                .when(logic)
                .isBookLoanedCheck(book.getId());

        assertThrows(ConflictException.class,
                () -> service.deactivate(1));

        verify(repo, never()).save(book);
    }

    @Test
    void deactivate_bookIsNotActive_throwsConflictException(){
        Book book = getBook(false);

        when(repo.findById(1))
                .thenReturn(Optional.of(book));

        doThrow(ConflictException.class)
                .when(logic).isBookActiveCheck(book);

        assertThrows(ConflictException.class,
                () -> service.deactivate(1));

        verify(logic, never())
                .isBookLoanedCheck(book.getId());

        verify(repo, never()).save(book);
    }

    @Test
    void deactivate_bookDoesNotExist_throwsNotFoundException(){
        when(repo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.deactivate(1));

        verifyNoInteractions(logic);

        verify(repo , never())
                .save(any(Book.class));
    }

    @Test
    void deactivate_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class,
                () -> service.deactivate(-1));

        verifyNoInteractions(logic);
        verifyNoInteractions(repo);
    }

    private Book getBook(boolean isActive){
        return new Book(1,
                "Clean Code",
                "John Doe",
                "abc124",
                20,
                20,
                isActive
        );
    }
}
