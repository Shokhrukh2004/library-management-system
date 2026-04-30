package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookResponse;
import org.example.book.dto.BookUpdateRequest;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repo;

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public void save(BookCreateRequest request) {
        BookValidator.validateCreateRequest(request);
        isbnDuplicateCheck(request.getIsbn());

        repo.save(BookParser.toBookFromCreateRequest(request));
    }

    public BookResponse findById(int id){
        Validator.validatePositiveInt(id, "Id");
        Book book = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + id));

        return BookParser.toBookResponse(book);
    }

    public List<BookResponse> findAll(){
        List<Book> books = repo.findAll();
        if(books.isEmpty()){
            return List.of();
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();

    }

    public List<BookResponse> findByTitle(String title){
        Validator.validateString(title, "Title");
        List<Book>  books = repo.findByTitle(title);
        if(books.isEmpty()){
            return List.of();
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByAuthor(String author){
        Validator.validateString(author, "Author");
        List<Book>  books = repo.findByAuthor(author);
        if(books.isEmpty()){
            return List.of();
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public void update(BookUpdateRequest book){
        BookValidator.validateUpdateRequest(book);
        Book book1 = repo.findById(book.getId())
                .orElseThrow(() -> new NotFoundException("Book not found with id " + book.getId()));

        book1.setTotalCopies(book.getTotalCopies());
        book1.setAvailableCopies(book.getAvailableCopies());

        repo.update(book1);
    }

    public void delete(int id){
        Validator.validatePositiveInt(id, "Id");
        repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + id));
        repo.delete(id);
    }

    private void isbnDuplicateCheck(String isbn){
        boolean duplicate = findAll().stream()
                .anyMatch(book -> book.getIsbn().equals(isbn));
        if(duplicate){
            throw new ConflictException("Book with ISBN " + isbn + " already exists");
        }
    }
}
