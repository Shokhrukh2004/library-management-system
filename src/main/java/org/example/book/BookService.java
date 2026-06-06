package org.example.book;

import org.example.book.dto.BookCreateRequest;
import org.example.book.dto.BookResponse;
import org.example.book.dto.BookUpdateRequest;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.validation.BookValidator;
import org.example.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository repo;
    private final LoanRepository loanRepo;

    public BookService(BookRepository repo, LoanRepository loanRepo) {
        this.repo = repo;
        this.loanRepo = loanRepo;
    }

    public void save(BookCreateRequest request) {
        log.info("Adding new book - isbn: {}", request.getIsbn());
        BookValidator.validateCreateRequest(request);
        isbnDuplicateCheck(request.getIsbn());

        repo.save(BookParser.toBookFromCreateRequest(request));
        log.info("Added new book successfully - isbn: {}", request.getIsbn());
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
            throw new NotFoundException("No books found");
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();

    }

    public List<BookResponse> findByTitle(String title){
        Validator.validateString(title, "Title");
        List<Book>  books = repo.findByTitle(title);
        if(books.isEmpty()){
            throw new NotFoundException("No books found by title " + title);
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByAuthor(String author){
        Validator.validateString(author, "Author");
        List<Book> books = repo.findByAuthor(author);
        if(books.isEmpty()){
            throw new NotFoundException("No books found by author " + author);
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public void update(BookUpdateRequest book){
        log.info("updating book - id: {}", book.getId());
        BookValidator.validateUpdateRequest(book);

        Book book1 = getBookIfExist(book.getId());
        isBookActiveCheck(book1);

        book1.setTotalCopies(book.getTotalCopies());
        book1.setAvailableCopies(book.getAvailableCopies());

        repo.update(book1);
        log.info("updated book successfully - id: {}", book.getId());
    }

    public void deactivate(int id){
        log.info("deactivating book - id: {}", id);
        Validator.validatePositiveInt(id, "Id");

        Book book = getBookIfExist(id);
        isBookActiveCheck(book);
        isBookLoanedCheck(id);

        repo.deactivate(id);
        log.info("deactivated book successfully - id: {}", id);
    }

    public void activate(int id){
        log.info("activating book - id: {}", id);
        Validator.validatePositiveInt(id, "Id");

        Book book = getBookIfExist(id);
        isBookNotActiveCheck(book);

        repo.activate(id);
        log.info("activated book successfully - id: {}", id);
    }

    public List<BookResponse> findAllInactive(){
        List<Book> books = repo.findAllInactive();
        if(books.isEmpty()){
            throw new NotFoundException("No Inactive books found");
        }

        return  books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }


    private void isbnDuplicateCheck(String isbn){
        if(repo.findByIsbn(isbn).isPresent()){
            log.warn("Book with ISBN {} already exists", isbn);
            throw new ConflictException("Book with ISBN " + isbn + " already exists");
        }
    }

    private void isBookLoanedCheck(int bookId) {
        boolean isBorrowed = loanRepo.findByBookId(bookId)
                .stream()
                .anyMatch(loan -> loan.getStatus().equals(Status.ACTIVE) ||
                        loan.getStatus().equals(Status.OVERDUE));

        if (isBorrowed) {
            log.warn("Book is already loaned - id: {}", bookId);
            throw new ConflictException("Book with ID " + bookId + " is loaned");
        }
    }

    private void isBookActiveCheck(Book book) {
        if(!book.isActive()){
            log.warn("Book is not active - id: {}", book.getId());
            throw new ConflictException("Book is not active");
        }
    }

    private void isBookNotActiveCheck(Book book){
        if(book.isActive()){
            log.warn("Book is active already - id: {}", book.getId());
            throw new ConflictException("Book is active already");
        }
    }

    private Book getBookIfExist(int id){
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));
    }
}
