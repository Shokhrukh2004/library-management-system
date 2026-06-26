package org.example.member;

import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.example.member.MemberUtil.getMember;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberActivateDeactivateTest {

    @Mock
    MemberRepository repo;

    @Mock
    MemberConflictLogic logic;

    @InjectMocks
    MemberService service;

    //          activate method test classes
    @Test
    void activate_validRequest_memberActivated(){
        Member member = getMember(false);

        when(repo.findById(member.getId()))
                .thenReturn(Optional.of(member));

        doNothing().when(logic)
                .isMemberNotActiveCheck(member);

        service.activate(member.getId());

        assertTrue(member.isActive());
        verify(repo).save(member);
    }

    @Test
    void activate_memberActiveAlready_throwsConflictException(){
        Member member = getMember(true);

        when(repo.findById(member.getId()))
                .thenReturn(Optional.of(member));

        doThrow(ConflictException.class)
                .when(logic)
                .isMemberNotActiveCheck(member);

        assertThrows(ConflictException.class,
                () -> service.activate(member.getId()));

        verify(repo, never()).save(member);
    }

    @Test
    void activate_memberDoesNotExist_throwsNotFoundException(){
        when(repo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.activate(1));

        verifyNoInteractions(logic);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void activate_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class,
                () -> service.activate(-1));

        verifyNoInteractions(logic);
        verifyNoInteractions(repo);
    }


    //          deactivate method test classes
    @Test
    void deactivate_validRequest_memberDeactivated(){
        Member member = getMember(true);

        when(repo.findById(member.getId()))
                .thenReturn(Optional.of(member));

        doNothing().when(logic)
                .isMemberActiveCheck(member);

        doNothing().when(logic)
                .isMemberLoanedCheck(member.getId());

        service.deactivate(member.getId());

        assertFalse(member.isActive());
        verify(repo).save(member);
    }

    @Test
    void deactivate_memberHasActiveLoan_throwsConflictException(){
        Member member = getMember(true);

        when(repo.findById(member.getId()))
                .thenReturn(Optional.of(member));

        doNothing().when(logic)
                .isMemberActiveCheck(member);

        doThrow(ConflictException.class)
                .when(logic)
                .isMemberLoanedCheck(member.getId());

        assertThrows(ConflictException.class,
                () -> service.deactivate(member.getId()));

        assertTrue(member.isActive());
        verify(repo, never()).save(member);
    }

    @Test
    void deactivate_memberInactiveAlready_throwsConflictException(){
        Member member = getMember(false);

        when(repo.findById(member.getId()))
                .thenReturn(Optional.of(member));

        doThrow(ConflictException.class).when(logic)
                .isMemberActiveCheck(member);

        assertThrows(ConflictException.class,
                () -> service.deactivate(member.getId()));

        verify(logic, never())
                .isMemberLoanedCheck(member.getId());

        verify(repo, never()).save(member);
    }

    @Test
    void deactivate_memberDoesNotExist_throwsNotFoundException(){
        when(repo.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.deactivate(1));

        verifyNoInteractions(logic);
        verify(repo, never())
                .save(any(Member.class));
    }

    @Test
    void deactivate_invalidId_throwsValidationException(){
        assertThrows(ValidationException.class,
                () -> service.deactivate(-1));

        verifyNoInteractions(logic);
        verifyNoInteractions(repo);
    }

}
