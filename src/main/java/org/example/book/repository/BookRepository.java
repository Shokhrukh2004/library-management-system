package org.example.book.repository;

import org.example.book.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
     void save(Book book);

     Optional<Book> findById(int id);

     List<Book> findAll();

     List<Book> findByTitle(String title);

     List<Book> findByAuthor(String author);

     void update(Book book);

     void delete(int id);
}
