package org.example.book;

import org.example.book.dto.BookResponse;
import org.example.book.repository.BookRepository;
import org.example.exception.NotFoundException;
import org.example.loan.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BookServiceFindMethodsTest {

    @Mock
    BookRepository bookRepo;

    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    BookService bookService;


    //      Book findById method test cases
    @Test
    void findById_bookExists_returnBook(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc123", 20, 20, true);
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        assertEquals(book.getId(), bookService.findById(1).getId());
    }

    @Test
    void findById_bookDoesNotExist_throwsNotFoundException(){
        when(bookRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.findById(1));
    }


    //      Book findAll method test cases
    @Test
    void findAll_bookFound_returnBooks(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc123", 20, 20, true);
        when(bookRepo.findAll()).thenReturn(List.of(book));

        List<BookResponse> books = bookService.findAll();

        assertEquals(1, books.size());
        assertEquals(book.getId(), books.get(0).getId());
    }

    @Test
    void findAll_noBooks_throwsNotFoundException(){
        when(bookRepo.findAll()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.findAll());
    }


    //      Book findByTitle method test cases
    @Test
    void findByTitle_bookFound_returnBook(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc123", 20, 20, true);
        when(bookRepo.findByTitle("Clean Code")).thenReturn(List.of(book));

        List<BookResponse> books = bookService.findByTitle("Clean Code");

        assertEquals(1, books.size());
        assertEquals(book.getTitle(), books.get(0).getTitle());
    }

    @Test
    void findByTitle_noBooks_throwsNotFoundException(){
        when(bookRepo.findByTitle("Clean Code")).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.findByTitle("Clean Code"));
    }


    //      Book findByTitle method test cases
    @Test
    void findByAuthor_bookFound_returnBook(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc123", 20, 20, true);
        when(bookRepo.findByAuthor("John Doe")).thenReturn(List.of(book));

        List<BookResponse> books = bookService.findByAuthor("John Doe");

        assertEquals(1, books.size());
        assertEquals(book.getAuthor(), books.get(0).getAuthor());
    }

    @Test
    void findByAuthor_noBooks_throwsNotFoundException(){
        when(bookRepo.findByAuthor("John Doe")).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.findByAuthor("John Doe"));
    }


    //      Book findAllInactive method test cases
    @Test
    void findAllInactive_bookFound_returnBooks(){
        Book book = new Book(1, "Clean Code", "John Doe", "abc123", 20, 20, false);
        when(bookRepo.findAllInactive()).thenReturn(List.of(book));

        List<BookResponse> books = bookService.findAllInactive();
        assertEquals(1, books.size());
        assertEquals(book.getId(), books.get(0).getId());
    }

    @Test
    void findAllInactive_noBooks_throwsNotFoundException(){
        when(bookRepo.findAllInactive()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.findAllInactive());
    }
}
