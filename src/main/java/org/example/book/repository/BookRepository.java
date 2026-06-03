package org.example.book.repository;

import org.example.book.Book;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
     void save(Book book);

     Optional<Book> findById(int id);

     List<Book> findAll();

     List<Book> findByTitle(String title);

     List<Book> findByAuthor(String author);

     Optional<Book> findByIsbn(String isbn);

     void update(Book book);

     void deactivate(int id);

     void activate(int id);

     List<Book> findAllInactive();

     void increaseAvailableCopies(int id);

     void decreaseAvailableCopies(int id);
}
