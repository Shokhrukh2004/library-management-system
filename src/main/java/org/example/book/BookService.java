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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repo;
    private final LoanRepository loanRepo;

    public BookService(BookRepository repo, LoanRepository loanRepo) {
        this.repo = repo;
        this.loanRepo = loanRepo;
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
        BookValidator.validateUpdateRequest(book);
        Book book1 = repo.findById(book.getId())
                .orElseThrow(() -> new NotFoundException("Book not found with id " + book.getId()));

        book1.setTotalCopies(book.getTotalCopies());
        book1.setAvailableCopies(book.getAvailableCopies());

        repo.update(book1);
    }

    public void deactivate(int id){
        Validator.validatePositiveInt(id, "Id");
        Book book = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + id));

        if(!book.isActive()){
            throw new ConflictException("Book has been deactivated already");
        }

        isBookLoanedCheck(id);
        repo.deactivate(id);
    }

    public void activate(int id){
        Validator.validatePositiveInt(id, "Id");
        Book book = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with id " + id));

        if(book.isActive()){
            throw new ConflictException("Book is already active");
        }

        repo.activate(id);
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
            throw new ConflictException("Book with ISBN " + isbn + " already exists");
        }
    }

    private void isBookLoanedCheck(int bookId) {
        boolean isBorrowed = loanRepo.findByBookId(bookId)
                .stream()
                .anyMatch(loan -> loan.getStatus().equals(Status.ACTIVE) ||
                        loan.getStatus().equals(Status.OVERDUE));

        if (isBorrowed) {
            throw new ConflictException("Book with ID " + bookId + " is loaned");
        }
    }
}
