package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookResponse;
import org.example.book.dto.BookUpdateRequest;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository repo;
    private final BookConflictLogic logic;

    public BookService(BookRepository repo, BookConflictLogic logic) {
        this.repo = repo;
        this.logic = logic;
    }

    public void save(BookCreateRequest request) {
        log.info("Adding new book - isbn: {}", request.getIsbn());

        logic.isbnDuplicateCheck(request.getIsbn());

        repo.save(BookParser.toBookFromCreateRequest(request));
        log.info("Added new book successfully - isbn: {}", request.getIsbn());
    }

    public BookResponse findById(int id){
        Validator.validateInt(id, "Id");

        Book book = getBookIfExist(id);

        return BookParser.toBookResponse(book);
    }

    public List<BookResponse> findAll(){
        List<Book> books = repo.findByIsActive(true);

        isEmptyCheck(books, "");

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByTitle(String title){
        Validator.validateStr(title, "Title");
        List<Book>  books = repo.findByTitleContainingIgnoreCase(title);

        isEmptyCheck(books, "with title " + title);

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByAuthor(String author){
        Validator.validateStr(author, "Author");
        List<Book> books = repo.findByAuthorContainingIgnoreCase(author);

        isEmptyCheck(books, "with author " + author);

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findAllInactive(){
        List<Book> books = repo.findByIsActive(false);

        isEmptyCheck(books, "with status inactive");

        return  books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }


    public void update(BookUpdateRequest book){
        log.info("updating book - id: {}", book.getId());
        logic.validateCopies(book.getTotalCopies(), book.getAvailableCopies());

        Book book1 = getBookIfExist(book.getId());
        logic.isBookActiveCheck(book1);

        book1.setTotalCopies(book.getTotalCopies());
        book1.setAvailableCopies(book.getAvailableCopies());

        repo.save(book1);
        log.info("updated book successfully - id: {}", book.getId());
    }

    public void deactivate(int id){
        log.info("deactivating book - id: {}", id);
        Validator.validateInt(id, "Id");

        Book book = getBookIfExist(id);
        logic.isBookActiveCheck(book);
        logic.isBookLoanedCheck(id);

        book.setActive(false);
        repo.save(book);
        log.info("deactivated book successfully - id: {}", id);
    }

    public void activate(int id){
        log.info("activating book - id: {}", id);
        Validator.validateInt(id, "Id");

        Book book = getBookIfExist(id);
        logic.isBookNotActiveCheck(book);

        book.setActive(true);
        repo.save(book);
        log.info("activated book successfully - id: {}", id);
    }


    private Book getBookIfExist(int id){
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found - id: {}", id);
                    return new NotFoundException("Book with id " + id + " not found");
                });
    }

    private <T> void isEmptyCheck(List<T> items, String field){
        if(items.isEmpty()){
            throw new NotFoundException("No Books found " + field);
        }
    }
}
