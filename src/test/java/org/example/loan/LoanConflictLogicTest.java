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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanConflictLogicTest {

    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    LoanConflictLogic logic;

    @Test
    void checkCreateRequest_validRequest_doesNotThrow(){
        Member member = getMember(true);
        Book book = getBook(true, true);

        when(loanRepo.findByMember_Id(member.getId()))
                .thenReturn(List.of());

        when(loanRepo.findByMember_IdAndBook_IdAndStatus(anyInt(), anyInt(), any(Status.class)))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> logic.checkCreateRequest(member, book));
    }

    @Test
    void checkCreateRequest_hasBorrowedSameBook_throwsConflictException(){
        Member member = getMember(true);
        Book book = getBook(true, true);
        Loan loan = getLoan(false, Status.ACTIVE);

        when(loanRepo.findByMember_Id(member.getId()))
                .thenReturn(List.of());

        when(loanRepo.findByMember_IdAndBook_IdAndStatus(
                member.getId(),
                book.getId(),
                Status.ACTIVE))
                .thenReturn(Optional.of(loan));

        assertThrows(ConflictException.class,
                () -> logic.checkCreateRequest(member, book));
    }

    @Test
    void checkCreateRequest_bookIsNotAvailable_throwsConflictException(){
        Member member = getMember(true);
        Book book = getBook(true, false);

        when(loanRepo.findByMember_Id(member.getId()))
                .thenReturn(List.of());

        assertThrows(ConflictException.class,
                () -> logic.checkCreateRequest(member, book));

        verify(loanRepo, never()).findByMember_IdAndBook_IdAndStatus(
                        anyInt(),
                        anyInt(),
                        any(Status.class));
    }

    @Test
    void checkCreateRequest_memberIsNotActive_throwsConflictException(){
        Member member = getMember(false);
        Book book = getBook(true, true);

        when(loanRepo.findByMember_Id(member.getId()))
                .thenReturn(List.of());

        assertThrows(ConflictException.class,
                () -> logic.checkCreateRequest(member, book));

        verify(loanRepo, never()).findByMember_IdAndBook_IdAndStatus(
                anyInt(),
                anyInt(),
                any(Status.class));
    }

    @Test
    void checkCreateRequest_bookIsNotActive_throwsConflictException(){
        Book book = getBook(false, true);
        Member member = getMember(true);

        when(loanRepo.findByMember_Id(1))
                .thenReturn(List.of());

        assertThrows(ConflictException.class,
                () -> logic.checkCreateRequest(member, book));

        verify(loanRepo, never()).findByMember_IdAndBook_IdAndStatus(
                anyInt(),
                anyInt(),
                any(Status.class)
        );
    }

    @Test
    void checkCreateRequest_hasOverdue_throwsConflictException(){
        Member member = getMember(true);
        Book book = getBook(true, true);
        Loan loan = getLoan(false, Status.OVERDUE);

        when(loanRepo.findByMember_Id(member.getId()))
                .thenReturn(List.of(loan));

        assertThrows(ConflictException.class,
                () -> logic.checkCreateRequest(member, book));

        verify(loanRepo, never()).findByMember_IdAndBook_IdAndStatus(
                anyInt(),
                anyInt(),
                any(Status.class)
        );
    }

    @Test
    void checkReturned_notReturned_doesNotThrow(){
        Loan loan = getLoan(false, Status.ACTIVE);

        assertDoesNotThrow(() -> logic.checkReturned(loan));
    }

    @Test
    void checkReturned_returnedAlready_throwsConflictException(){
        Loan loan = getLoan(true, Status.RETURNED);

        assertThrows(ConflictException.class,
                () -> logic.checkReturned(loan));
    }

    private Book getBook(boolean isActive, boolean isAvailable){
        return new Book(
                1,
                "Clean Code",
                "John Doe",
                "abc123",
                20,
                isAvailable ? 20 : 0,
                isActive
        );
    }

    private Member getMember(boolean isActive){
        return new Member(
                1,
                "Will Smith",
                "will@mail.ru",
                LocalDate.now(),
                isActive
        );
    }

    private Loan getLoan(boolean isReturned, Status status){
        return new Loan(
                1,
                getMember(true),
                getBook(true, true),
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                isReturned ? LocalDate.now().plusDays(3) : null,
                status
        );
    }
}
