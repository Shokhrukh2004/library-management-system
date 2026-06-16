package org.example.member;

import org.example.exception.ConflictException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberConflictLogicTest {

    @Mock
    MemberRepository memberRepo;

    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    MemberConflictLogic logic;

    //              emailDuplicateCheck method test cases
    @Test
    void emailDuplicateCheck_validEmail_doesNotThrow(){
        when(memberRepo.findByEmail(any(String.class)))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() ->logic.emailDuplicateCheck("email"));
    }

    @Test
    void emailDuplicateCheck_emailExists_throwsConflictException(){
        when(memberRepo.findByEmail(any(String.class)))
                .thenReturn(Optional.of(new Member()));

        assertThrows(ConflictException.class,
                () -> logic.emailDuplicateCheck("email"));
    }


    //              isMemberLoanedCheck method test cases
    @Test
    void isMemberLoanedCheck_memberNotLoaned_doesNotThrow(){

        when(loanRepo.existsByMember_IdAndStatus(anyInt(), any(Status.class)))
                .thenReturn(false);


        assertDoesNotThrow(() -> logic.isMemberLoanedCheck(1));
    }

    @Test
    void isMemberLoanedCheck_memberLoaned_throwsConflictException(){
        when(loanRepo.existsByMember_IdAndStatus(anyInt(), any(Status.class)))
                .thenReturn(true);

        assertThrows(ConflictException.class,
                () -> logic.isMemberLoanedCheck(1));
    }


    //              isMemberActiveCheck method test cases
    @Test
    void isMemberActiveCheck_memberActive_doesNotThrow(){
        Member member = MemberUtil.getMember(true);

        assertDoesNotThrow(() -> logic.isMemberActiveCheck(member));
    }

    @Test
    void isMemberActiveCheck_memberNotActive_throwsConflictException(){
        Member member = MemberUtil.getMember(false);

        assertThrows(ConflictException.class,
                () -> logic.isMemberActiveCheck(member));
    }


    //              isMemberNotActiveCheck method test cases
    @Test
    void isMemberNotActiveCheck_memberNotActive_doesNotThrow(){
        Member member = MemberUtil.getMember(false);

        assertDoesNotThrow(() -> logic.isMemberNotActiveCheck(member));
    }

    @Test
    void isMemberNotActiveCheck_memberActive_throwsConflictException(){
        Member member = MemberUtil.getMember(true);

        assertThrows(ConflictException.class,
                () -> logic.isMemberNotActiveCheck(member));
    }
}
