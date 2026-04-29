package org.example.service;

import org.example.dto.BookCreateRequest;
import org.example.dto.BookResponse;
import org.example.dto.BookUpdateRequest;
import org.example.model.Book;
import org.example.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public void save(BookCreateRequest book){
        validateCreateRequest(book);
        repo.save(toBookFromCreateRequest(book));
    }

    public BookResponse findById(int id){
        validateInt(id, "Id");
        Book book = repo.findById(id).orElseThrow(() ->
                new NoSuchElementException("Book not found"));

        return toBookResponse(book);
    }

    public List<BookResponse> findAll(){

        List<BookResponse> responses = repo.findAll()
                .stream()
                .map(this::toBookResponse)
                .toList();

        if(responses.isEmpty()){
            throw new NoSuchElementException("No books found");
        }

        return responses;
    }

    public List<BookResponse> findByTitle(String title){
        validateStr(title, "Title");
        List<Book> books = repo.findByTitle(title);
        if(books.isEmpty()){
            throw new NoSuchElementException("Book with Title " + title + " not found");
        }

        return books.stream()
                .map(this::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByAuthor(String author){
        validateStr(author, "Author");
        List<Book> books = repo.findByAuthor(author);
        if(books.isEmpty()){
            throw new NoSuchElementException("Book with Author " + author + " not found");
        }

        return books.stream()
                .map(this::toBookResponse)
                .toList();
    }

    public void update(BookUpdateRequest book){
        validateUpdateRequest(book);
        BookResponse book1 = findById(book.getId());

        repo.update(new Book(
                book.getId(),
                book1.getTitle(),
                book1.getAuthor(),
                book1.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        ));
    }

    public void delete(int id){
        validateInt(id, "Id");
        Optional<Book> book = repo.findById(id);
        book.orElseThrow(() -> new NoSuchElementException("Book with id " + id + " not found"));
        repo.delete(id);
    }





    private void validateCreateRequest(BookCreateRequest book){
        if(book == null){
            throw new IllegalArgumentException("Book Request is null");
        }

        validateStr(book.getTitle(), "Title");
        validateStr(book.getAuthor(), "Author");
        validateStr(book.getIsbn(), "ISBN");
        validateIsbn(book.getIsbn());
        validateInt(book.getTotalCopies(), "Total Copies");
    }

    private void validateUpdateRequest(BookUpdateRequest book){
        if(book == null){
            throw new IllegalArgumentException("Book Request is null");
        }
        validateInt(book.getId(), "Id");
        validateInt(book.getTotalCopies(), "Total Copies");
        validateInt(book.getAvailableCopies(), "Available Copies");
    }

    private void validateInt(int num, String field){
        if(num < 1){
            throw new IllegalArgumentException("Book " + field +" must be greater than 0");
        }
    }

    private void validateStr(String word, String name){
        if(word == null || word.isBlank() || word.length() < 3){
            throw new IllegalArgumentException("Book " + name + " must have at least 3 characters");
        }
    }

    private void validateIsbn(String ibn){
        validateStr(ibn, "ISBN");
        boolean duplicate = repo.findAll()
                .stream()
                .anyMatch(book -> book.getIsbn().equals(ibn));

        if(duplicate){
            throw new IllegalArgumentException("Book with ISBN " + ibn + " already exists");
        }
    }


    private Book toBookFromCreateRequest(BookCreateRequest request){

        return new Book(
                0,
                request.getTitle().trim(),
                request.getAuthor().trim(),
                request.getIsbn().trim().trim(),
                request.getTotalCopies(),
                request.getTotalCopies()
        );
    }

    private BookResponse toBookResponse(Book book){


        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getTotalCopies(),
                book.getAvailableCopies()
        );
    }
}
