package org.example.ui;

import org.example.book.Book;
import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookResponse;

import java.util.List;
import java.util.Optional;

public interface UInterface {

    void save(BookCreateRequest book);

    int findById(int id);

    List<Book> findAll();

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    void update(Book book);

    void delete(int id);
}
