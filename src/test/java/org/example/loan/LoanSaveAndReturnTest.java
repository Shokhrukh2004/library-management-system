package org.example.loan;

import org.example.book.Book;
import org.example.book.repository.BookRepository;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.loan.dto.LoanCreateRequest;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.Member;
import org.example.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanSaveAndReturnTest {

    @Mock
    LoanRepository loanRepo;

    @Mock
    BookRepository bookRepo;

    @Mock
    MemberRepository memberRepo;

    @Mock
    LoanConflictLogic logic;

    @InjectMocks
    LoanService service;


    //          save method test cases
    @Test
    void save_validRequest_loanSaved(){
        LoanCreateRequest request = new LoanCreateRequest(1, 1);
        Member member = getMember();
        Book book = getBook();
        int initialCopies = book.getAvailableCopies();

        when(memberRepo.findById(request.getMemberId()))
                .thenReturn(Optional.of(member));

        when(bookRepo.findById(request.getBookId()))
                .thenReturn(Optional.of(book));

        doNothing()
                .when(logic)
                .checkCreateRequest(member, book);

        service.save(request);

        ArgumentCaptor<Loan> captor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepo).save(captor.capture());
        Loan loan = captor.getValue();

        assertAll(
                () -> assertEquals(initialCopies,
                        book.getAvailableCopies() + 1),
                () -> assertEquals(member.getId(),
                        loan.getMember().getId()),
                () -> assertEquals(book.getId(),
                        loan.getBook().getId()),
                () -> assertEquals(loan.getBorrowDate().plusDays(10),
                        loan.getDueDate()),
                () -> assertNull(loan.getReturnDate()),
                () -> assertEquals(Status.ACTIVE, loan.getEffectiveStatus())
        );

        verify(bookRepo).save(book);
    }

    @Test
    void save_invalidRequest_throwsConflictException(){
        LoanCreateRequest request = new LoanCreateRequest(1, 1);
        Member member = new Member(1, "John Doe", "john@mail.ru", LocalDate.now(), true);
        Book book = new Book(1, "Clean Code", "Johny Depp", "abc123", 20, 20, true);

        when(memberRepo.findById(request.getMemberId()))
                .thenReturn(Optional.of(member));
        when(bookRepo.findById(request.getBookId()))
                .thenReturn(Optional.of(book));

        doThrow(ConflictException.class)
                .when(logic)
                .checkCreateRequest(member, book);

        assertThrows(ConflictException.class,
                () -> service.save(request));

        verify(loanRepo, never()).save(any(Loan.class));
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void save_memberDoesNotExist_throwsConflictException(){
        LoanCreateRequest request = new LoanCreateRequest(1, 1);
        Book book = new Book(1, "Clean Code", "Johny Depp", "abc123", 20, 20, true);

        when(bookRepo.findById(request.getBookId()))
                .thenReturn(Optional.of(book));

        when(memberRepo.findById(request.getMemberId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.save(request));

        verifyNoInteractions(logic);
        verify(loanRepo, never()).save(any(Loan.class));
        verify(bookRepo, never()).save(any(Book.class));
    }

    @Test
    void save_bookDoesNotExist_throwsConflictException(){
        LoanCreateRequest request = new LoanCreateRequest(1, 1);

        when(bookRepo.findById(request.getBookId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.save(request));

        verifyNoInteractions(memberRepo);
        verifyNoInteractions(logic);
        verify(loanRepo, never()).save(any(Loan.class));
    }


    // returnBook method test cases
    @Test
    void returnBook_validRequest_bookReturned(){
        Loan loan = getLoan();
        int initialCopies = loan.getBook().getAvailableCopies();

        when(loanRepo.findById(loan.getId()))
                .thenReturn(Optional.of(loan));

        doNothing()
                .when(logic)
                .checkReturned(loan);

        service.returnBook(loan.getId());

        assertAll(
                () -> assertEquals(initialCopies, loan.getBook().getAvailableCopies() - 1),
                () -> assertEquals(Status.RETURNED, loan.getEffectiveStatus()),
                () -> assertNotNull(loan.getReturnDate())
        );

        verify(loanRepo).save(loan);
        verify(bookRepo).save(loan.getBook());
    }

    @Test
    void returnBook_loanReturnedAlready_throwsConflictException(){
        Loan loan = getLoan();

        when(loanRepo.findById(loan.getId()))
                .thenReturn(Optional.of(loan));

        doThrow(ConflictException.class)
                .when(logic).checkReturned(loan);

        assertThrows(ConflictException.class,
                () -> service.returnBook(loan.getId()));

        verifyNoInteractions(bookRepo);
        verify(loanRepo, never()).save(loan);
    }

    @Test
    void returnBook_loanDoesNotExist_throwsNotFoundException(){
        when(loanRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.returnBook(1));

        verifyNoInteractions(bookRepo);
        verifyNoInteractions(logic);
        verify(loanRepo, never()).save(any(Loan.class));
    }

    @Test
    void returnBook_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class,
                () -> service.returnBook(-1));

        verifyNoInteractions(bookRepo);
        verifyNoInteractions(logic);
        verifyNoInteractions(loanRepo);
    }


    //          util methods
    private Book getBook(){
        return new Book(1,
                "Clean Code",
                "Johny Depp",
                "abc123",
                20,
                20,
                true);
    }

    private Member getMember() {
        return new Member(1,
                "John Doe",
                "john@mail.ru",
                LocalDate.now(),
                true);
    }

    private Loan getLoan() {
        return new Loan(
                1,
                getMember(),
                getBook(),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                null,
                Status.ACTIVE
        );
    }
}
