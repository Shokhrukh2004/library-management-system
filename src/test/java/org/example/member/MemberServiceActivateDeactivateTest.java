package org.example.member;

import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.loan.Loan;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceActivateDeactivateTest {

    @Mock
    MemberRepository memberRepo;

    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    MemberService memberService;

    //      activate method test cases
    @Test
    void activate_validRequest_memberIsActivated() {
        Member member = new Member(1, "John Doe", "john@mail.ru", LocalDate.now(), false);

        when(memberRepo.findById(member.getId())).thenReturn(Optional.of(member));

        memberService.activate(member.getId());

        verify(memberRepo).activate(member.getId());
    }

    @Test
    void activate_memberActiveAlready_throwsConflictException() {
        Member member = new Member(1, "John Doe", "john@mail.ru", LocalDate.now(), true);

        when(memberRepo.findById(member.getId())).thenReturn(Optional.of(member));

        assertThrows(ConflictException.class, () -> memberService.activate(member.getId()));

        verify(memberRepo, never()).activate(anyInt());
    }

    @Test
    void activate_memberDoesNotExist_throwsNotFoundException() {
        when(memberRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberService.activate(1));

        verify(memberRepo, never()).activate(anyInt());
    }

    @Test
    void activate_invalidMemberId_throwsValidationException() {
        assertThrows(ValidationException.class, () -> memberService.activate(-10));

        verifyNoInteractions(memberRepo);
    }


    //        deactivate method test cases
    @Test
    void deactivate_validRequest_memberIsDeactivated() {
        Member member = new Member(1, "John Doe", "john@mail.ru", LocalDate.now(), true);

        when(memberRepo.findById(member.getId())).thenReturn(Optional.of(member));
        when(loanRepo.findByMemberId(member.getId())).thenReturn(List.of());

        memberService.deactivate(member.getId());

        verify(memberRepo).deactivate(member.getId());
    }

    @Test
    void deactivate_memberLoaned_throwsConflictException() {
        Member member = new Member(1, "John Doe", "john@mail.ru", LocalDate.now(), true);
        Loan loan = new Loan(1, 1, 1, null, null, null, Status.ACTIVE);

        when(memberRepo.findById(member.getId())).thenReturn(Optional.of(member));
        when(loanRepo.findByMemberId(member.getId())).thenReturn(List.of(loan));

        assertThrows(ConflictException.class, () -> memberService.deactivate(member.getId()));

        verify(memberRepo,  never()).deactivate(member.getId());
    }

    @Test
    void deactivate_memberInactiveAlready_throwsConflictException() {
        Member member = new Member(1, "John Doe", "john@mail.ru", LocalDate.now(), false);

        when(memberRepo.findById(member.getId())).thenReturn(Optional.of(member));

        assertThrows(ConflictException.class, () -> memberService.deactivate(member.getId()));

        verify(memberRepo, never()).deactivate(member.getId());
    }

    @Test
    void deactivate_memberDoesNotExist_throwsNotFoundException() {
        when(memberRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberService.deactivate(1));
    }

    @Test
    void deactivate_invalidMemberId_throwsValidationException() {
        assertThrows(ValidationException.class, () -> memberService.deactivate(-1));

        verifyNoInteractions(memberRepo);
    }
}
