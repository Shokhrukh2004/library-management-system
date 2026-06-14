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
    private final LoanRepository loanRepo;

    public BookService(BookRepository repo, LoanRepository loanRepo) {
        this.repo = repo;
        this.loanRepo = loanRepo;
    }

    public void save(BookCreateRequest request) {
        log.info("Adding new book - isbn: {}", request.getIsbn());

        isbnDuplicateCheck(request.getIsbn());

        repo.save(BookParser.toBookFromCreateRequest(request));
        log.info("Added new book successfully - isbn: {}", request.getIsbn());
    }

    public BookResponse findById(int id){
        Validator.validateInt(id, "Id");

        Book book = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + id));

        return BookParser.toBookResponse(book);
    }

    public List<BookResponse> findAll(){
        List<Book> books = repo.findByIsActive(true);
        if(books.isEmpty()){
            throw new NotFoundException("No books found");
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByTitle(String title){
        Validator.validateStr(title, "Title");
        List<Book>  books = repo.findByTitleContainingIgnoreCase(title);
        if(books.isEmpty()){
            throw new NotFoundException("No books found by title " + title);
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findByAuthor(String author){
        Validator.validateStr(author, "Author");
        List<Book> books = repo.findByAuthorContainingIgnoreCase(author);
        if(books.isEmpty()){
            throw new NotFoundException("No books found by author " + author);
        }

        return books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }

    public List<BookResponse> findAllInactive(){
        List<Book> books = repo.findByIsActive(false);

        if(books.isEmpty()){
            throw new NotFoundException("No Inactive books found");
        }

        return  books.stream()
                .map(BookParser::toBookResponse)
                .toList();
    }


    public void update(BookUpdateRequest book){
        log.info("updating book - id: {}", book.getId());
        validateCopies(book.getTotalCopies(), book.getAvailableCopies());

        Book book1 = getBookIfExist(book.getId());
        isBookActiveCheck(book1);

        book1.setTotalCopies(book.getTotalCopies());
        book1.setAvailableCopies(book.getAvailableCopies());

        repo.save(book1);
        log.info("updated book successfully - id: {}", book.getId());
    }

    public void deactivate(int id){
        log.info("deactivating book - id: {}", id);
        Validator.validateInt(id, "Id");

        Book book = getBookIfExist(id);
        isBookActiveCheck(book);
        isBookLoanedCheck(id);

        book.setActive(false);
        repo.save(book);
        log.info("deactivated book successfully - id: {}", id);
    }

    public void activate(int id){
        log.info("activating book - id: {}", id);
        Validator.validateInt(id, "Id");

        Book book = getBookIfExist(id);
        isBookNotActiveCheck(book);

        book.setActive(true);
        repo.save(book);
        log.info("activated book successfully - id: {}", id);
    }





    private void isbnDuplicateCheck(String isbn){
        if(repo.findByIsbn(isbn).isPresent()){
            log.warn("Book with ISBN {} already exists", isbn);
            throw new ConflictException("Book with ISBN " + isbn + " already exists");
        }
    }

    private void isBookLoanedCheck(int bookId) {
        boolean isBorrowed = loanRepo.findByBook_Id(bookId)
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
                .orElseThrow(() -> {
                    log.warn("Book not found - id: {}", id);
                    return new NotFoundException("Book with id " + id + " not found");
                });
    }

    private static void validateCopies(int totalCopies, int availableCopies){
        if(totalCopies < availableCopies){
            throw new ValidationException("Total Copies can't be less than Available Copies");
        }
    }
}
