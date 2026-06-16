package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookUpdateRequest;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceSaveAndUpdateTest {

    @Mock
    BookRepository repo;

    @Mock
    BookConflictLogic logic;

    @InjectMocks
    BookService service;

    //      save method test cases
    @Test
    void save_validRequest_bookIsSaved(){
        BookCreateRequest request = BookUtil.getCreateRequest();

        doNothing()
                .when(logic)
                .isbnDuplicateCheck(request.getIsbn());

        service.save(request);
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

        verify(repo).save(captor.capture());

        Book book = captor.getValue();

        assertAll(
                () -> assertEquals(request.getTitle(), book.getTitle()),
                () -> assertEquals(request.getAuthor(), book.getAuthor()),
                () -> assertEquals(request.getIsbn(), book.getIsbn()),
                () -> assertEquals(request.getTotalCopies(), book.getTotalCopies()),
                () -> assertEquals(request.getTotalCopies(), book.getAvailableCopies()),
                () -> assertTrue(book.isActive())
        );
    }

    @Test
    void save_duplicateIsbn_throwsConflictException(){
        BookCreateRequest request = BookUtil.getCreateRequest();

        doThrow(ConflictException.class)
                .when(logic)
                .isbnDuplicateCheck(request.getIsbn());

        assertThrows(ConflictException.class,
                () -> service.save(request));

        verify(repo, never())
                .save(any(Book.class));
    }


    //      update method test cases
    @Test
    void update_validRequest_bookIsUpdated(){
        BookUpdateRequest request = BookUtil.getUpdateRequest(true);
        Book book = BookUtil.getBook(true);

        doNothing()
                .when(logic)
                .validateCopies(request.getTotalCopies(), request.getAvailableCopies());

        when(repo.findById(request.getId()))
                .thenReturn(Optional.of(book));

        doNothing()
                .when(logic)
                .isBookActiveCheck(book);

        service.update(request);

        assertEquals(request.getTotalCopies(), book.getTotalCopies());
        assertEquals(request.getAvailableCopies(), book.getAvailableCopies());

        verify(repo).save(book);
    }

    @Test
    void update_bookIsNotActive_throwsConflictException(){
        BookUpdateRequest request = BookUtil.getUpdateRequest(true);
        Book book = BookUtil.getBook(false);

        doNothing()
                .when(logic)
                .validateCopies(request.getTotalCopies(), request.getAvailableCopies());

        when(repo.findById(request.getId()))
                .thenReturn(Optional.of(book));

        doThrow(ConflictException.class)
                .when(logic).isBookActiveCheck(book);

        assertThrows(ConflictException.class,
                () -> service.update(request));

        verify(repo, never())
                .save(any(Book.class));
    }

    @Test
    void update_bookDoesNotExist_throwsNotFoundException(){
        BookUpdateRequest request = BookUtil.getUpdateRequest(true);

        doNothing()
                .when(logic)
                .validateCopies(request.getTotalCopies(), request.getAvailableCopies());

        when(repo.findById(request.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.update(request));

        verify(logic, never())
                .isBookActiveCheck(any(Book.class));

        verify(repo, never())
                .save(any(Book.class));
    }

    @Test
    void update_invalidCopies_throwsConflictException(){
        BookUpdateRequest request = BookUtil.getUpdateRequest(false);
        doThrow(ConflictException.class)
                .when(logic)
                .validateCopies(request.getTotalCopies(), request.getAvailableCopies());

        assertThrows(ConflictException.class,
                () -> service.update(request));

        verify(repo, never())
                .findById(request.getId());

        verify(logic, never())
                .isBookActiveCheck(any(Book.class));

        verify(repo, never())
                .save(any(Book.class));
    }
}
