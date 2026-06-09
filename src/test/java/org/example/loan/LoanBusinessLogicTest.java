package org.example.loan;

import org.example.book.Book;
import org.example.exception.ConflictException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanBusinessLogicTest {
    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    LoanBusinessLogic loanBusinessLogic;

    //      checkCreateRequest method test cases
    @Test
    void checkCreateRequest_validRequest_doesNotThrow() {
        Book book = getBook(true, true);
        Member member = getMember(true);


        when(loanRepo.findByMemberId(member.getId()))
                .thenReturn(List.of());

        when(loanRepo.findActiveByMemberAndBook(member.getId(), book.getId()))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> loanBusinessLogic.checkCreateRequest(member, book));
    }

    @Test
    void checkCreateRequest_borrowedSameBookAlready_throwConflictException(){
        Book book = getBook(true, true);
        Member member = getMember(true);
        Loan loan = getLoan(Status.ACTIVE);

        when(loanRepo.findByMemberId(member.getId()))
                .thenReturn(List.of());

        when(loanRepo.findActiveByMemberAndBook(member.getId(), book.getId()))
                .thenReturn(Optional.of(loan));

        assertThrows(ConflictException.class,
                () -> loanBusinessLogic.checkCreateRequest(member, book));
    }

    @Test
    void checkCreateRequest_bookNotAvailable_throwsConflictException(){
        Book book = getBook(true, false);
        Member member = getMember(true);

        when(loanRepo.findByMemberId(member.getId()))
                .thenReturn(List.of());

        assertThrows(ConflictException.class,
                () -> loanBusinessLogic.checkCreateRequest(member, book));

        verify(loanRepo, never())
                .findActiveByMemberAndBook(anyInt(), anyInt());
    }

    @Test
    void checkCreateRequest_memberNotActive_throwsConflictException(){
        Book book = getBook(true, true);
        Member member = getMember(false);

        when(loanRepo.findByMemberId(member.getId()))
                .thenReturn(List.of());

        assertThrows(ConflictException.class,
                () -> loanBusinessLogic.checkCreateRequest(member, book));
    }

    @Test
    void checkCreateRequest_bookNotActive_throwsConflictException(){
        Book book = getBook(false, true);
        Member member = getMember(true);

        when(loanRepo.findByMemberId(member.getId()))
                .thenReturn(List.of());

        assertThrows(ConflictException.class,
                () -> loanBusinessLogic.checkCreateRequest(member, book));
    }

    @Test
    void checkCreateRequest_hasOverDueLoan_throwsConflictException(){
        Book book = getBook(true, true);
        Member member = getMember(true);
        Loan loan = getLoan(Status.OVERDUE);

        when(loanRepo.findByMemberId(member.getId()))
                .thenReturn(List.of(loan));

        assertThrows(ConflictException.class,
                () -> loanBusinessLogic.checkCreateRequest(member, book));
    }


    //         checkReturned method test cases
    @Test
    void checkReturned_notReturned_doesNotThrow(){
        Loan loan = getLoan(Status.ACTIVE);
        assertDoesNotThrow(() -> loanBusinessLogic.checkReturned(loan));
    }

    @Test
    void checkReturned_alreadyReturned_throwsConflictException(){
        Loan loan = getLoan(Status.RETURNED);

        assertThrows(ConflictException.class,
                () -> loanBusinessLogic.checkReturned(loan));
    }


    //      Helper methods
    private Book getBook(boolean isActive, boolean available) {
        return new Book(
                1,
                "Clean Code",
                "John Depp",
                "abc123",
                20, available ? 10 : 0,
                isActive
        );
    }

    private Member getMember(boolean isActive) {
        return new Member(
                1,
                "John Doe",
                "john@mail.ru",
                LocalDate.now(),
                isActive
        );
    }

    private Loan getLoan(Status status) {
        return new Loan(1, 1, 1, null, null, null, status);
    }

}
