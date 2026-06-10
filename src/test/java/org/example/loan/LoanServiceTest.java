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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    LoanRepository loanRepo;

    @Mock
    MemberRepository memberRepo;

    @Mock
    BookRepository bookRepo;

    @Mock
    LoanBusinessLogic loanLogic;

    @InjectMocks
    LoanService loanService;


    //      save method test cases
    @Test
    void save_validRequest_savedSuccessfully(){
        LoanCreateRequest request = new LoanCreateRequest(1, 1);
        Book book = getBook();
        Member member = getMember();

        when(memberRepo.findById(request.getMemberId()))
                .thenReturn(Optional.of(member));

        when(bookRepo.findById(request.getBookId()))
                .thenReturn(Optional.of(book));

        loanService.save(request);

        verify(loanLogic).checkCreateRequest(member, book);
        verify(bookRepo).decreaseAvailableCopies(book.getId());
        verify(loanRepo).save(any(Loan.class));
    }

    @Test
    void save_failedCheckCreateRequest_throwsConflictException() {
        LoanCreateRequest request = new LoanCreateRequest(1, 1);
        Book book = getBook();
        Member member = getMember();

        when(memberRepo.findById(request.getMemberId()))
                .thenReturn(Optional.of(member));

        when(bookRepo.findById(request.getBookId()))
                .thenReturn(Optional.of(book));

        doThrow(ConflictException.class).when(loanLogic)
                .checkCreateRequest(member, book);

        assertThrows(ConflictException.class, () -> loanService.save(request));

        verify(bookRepo, never()).decreaseAvailableCopies(anyInt());
        verify(loanRepo, never()).save(any(Loan.class));
    }

    @Test
    void save_memberDoesNotExist_throwsNotFoundException() {
        LoanCreateRequest request = new LoanCreateRequest(1, 1);
        Book book = getBook();

        when(bookRepo.findById(request.getBookId())).thenReturn(Optional.of(book));
        when(memberRepo.findById(request.getMemberId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanService.save(request));

        verify(loanLogic, never()).checkCreateRequest(any(Member.class), any(Book.class));
        verify(bookRepo, never()).decreaseAvailableCopies(anyInt());
        verify(loanRepo, never()).save(any(Loan.class));
    }

    @Test
    void save_bookDoesNotExist_throwsNotFoundException(){
        LoanCreateRequest request = new LoanCreateRequest(1, 1);

        when(bookRepo.findById(request.getBookId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanService.save(request));

        verify(memberRepo, never()).findById(request.getMemberId());
        verify(loanLogic, never()).checkCreateRequest(any(Member.class), any(Book.class));
        verify(bookRepo, never()).decreaseAvailableCopies(anyInt());
        verify(loanRepo, never()).save(any(Loan.class));
    }

    @Test
    void save_invalidIds_throwsValidationException(){
        LoanCreateRequest request = new LoanCreateRequest(-1, -1);

        assertThrows(ValidationException.class, () -> loanService.save(request));

        verifyNoInteractions(loanLogic);
        verifyNoInteractions(bookRepo);
        verifyNoInteractions(memberRepo);
        verifyNoInteractions(loanRepo);
    }


    //      returnBook method test cases
    @Test
    void returnBook_validRequest_returnedSuccessfully(){
        Loan loan = getLoan();

        when(loanRepo.findById(loan.getId())).thenReturn(Optional.of(loan));

        loanService.returnBook(loan.getId());

        verify(bookRepo).increaseAvailableCopies(loan.getBookId());
        verify(loanRepo).returnLoan(loan.getId());
    }

    @Test
    void returnBook_loanReturnedAlready_throwsConflictException() {
        Loan loan = getLoan();

        when(loanRepo.findById(loan.getId())).thenReturn(Optional.of(loan));
        doThrow(ConflictException.class).when(loanLogic).checkReturned(loan);

        assertThrows(ConflictException.class, () -> loanService.returnBook(loan.getId()));

        verify(bookRepo, never()).increaseAvailableCopies(loan.getBookId());
        verify(loanRepo, never()).returnLoan(loan.getId());
    }

    @Test
    void returnBook_loanNotFound_throwsNotFoundException() {

        when(loanRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanService.returnBook(1));

        verifyNoInteractions(bookRepo);
        verifyNoInteractions(loanLogic);
        verifyNoMoreInteractions(loanRepo);
    }

    @Test
    void returnBook_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class, () -> loanService.returnBook(-1));

        verifyNoInteractions(bookRepo);
        verifyNoInteractions(loanLogic);
        verifyNoMoreInteractions(loanRepo);
    }

    //      utility methods
    private Member getMember(){
        return new Member(
                1,
                "John Kennedy",
                "john@mail.ru",
                LocalDate.now().minusDays(10),
                true
        );
    }

    private Book getBook(){
        return new Book(
                1,
                "Clean Code",
                "Robert Ferguson",
                "abc123",
                10,
                10,
                true
        );
    }

    private Loan getLoan(){
        return new Loan(
                1,
                1,
                1,
                LocalDate.now().minusDays(3),
                LocalDate.now().plusDays(7),
                null,
                Status.ACTIVE
        );
    }

}
