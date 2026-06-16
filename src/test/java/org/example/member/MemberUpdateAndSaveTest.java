package org.example.member;

import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberUpdateRequest;
import org.example.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.example.member.MemberUtil.getMember;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberUpdateAndSaveTest {

    @Mock
    MemberRepository repo;

    @Mock
    MemberConflictLogic logic;

    @InjectMocks
    MemberService service;


    //              save method test cases
    @Test
    void save_validRequest_memberSaved(){
        MemberCreateRequest request = MemberUtil.getCreateRequest(true);

        doNothing().when(logic)
                .emailDuplicateCheck(request.getEmail());

        service.addMember(request);
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        verify(repo).save(captor.capture());
        Member member = captor.getValue();

        assertAll(
                () -> assertEquals(request.getName(),
                        member.getName()),
                () -> assertEquals(request.getEmail(),
                        member.getEmail()),
                () -> assertNotNull(member.getRegisterDate()),
                () -> assertTrue(member.isActive())
        );
        verify(repo).save(member);
    }

    @Test
    void save_emailExists_throwsConflictException(){
        MemberCreateRequest request = MemberUtil.getCreateRequest(true);

        doThrow(ConflictException.class)
                .when(logic)
                .emailDuplicateCheck(request.getEmail());

        assertThrows(ConflictException.class,
                () -> service.addMember(request));

        verify(repo, never()).save(any(Member.class));
    }

    @Test
    void save_invalidEmail_throwsValidationException(){
        MemberCreateRequest request = MemberUtil.getCreateRequest(false);

        assertThrows(ValidationException.class,
                () -> service.addMember(request));


        verifyNoInteractions(logic);
        verifyNoInteractions(repo);
    }


    //          update method test cases
    @Test
    void update_validRequest_memberUpdated(){
        MemberUpdateRequest request = MemberUtil.getUpdateRequest(true);
        Member member = getMember(true);

        when(repo.findById(request.getId()))
                .thenReturn(Optional.of(member));

        doNothing()
                .when(logic)
                .emailDuplicateCheck(request.getEmail());

        service.update(request);

        assertEquals(request.getName(),  member.getName());
        assertEquals(request.getEmail(),  member.getEmail());

        verify(repo).save(member);
    }

    @Test
    void update_emailExists_throwsConflictException(){
        MemberUpdateRequest request = MemberUtil.getUpdateRequest(true);
        Member member = getMember(true);

        when(repo.findById(request.getId()))
                .thenReturn(Optional.of(member));

        doThrow(ConflictException.class)
                .when(logic)
                .emailDuplicateCheck(request.getEmail());

        assertThrows(ConflictException.class,
                () -> service.update(request));

        verify(repo, never()).save(any(Member.class));
    }

    @Test
    void update_memberNotFound_throwsNotFoundException(){
        MemberUpdateRequest request = MemberUtil.getUpdateRequest(true);

        when(repo.findById(request.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.update(request));

        verifyNoInteractions(logic);
        verify(repo, never()).save(any(Member.class));
    }

    @Test
    void update_invalidEmail_throwsValidationException(){
        MemberUpdateRequest request = MemberUtil.getUpdateRequest(false);

        assertThrows(ValidationException.class,
                () -> service.update(request));

        verifyNoInteractions(logic);
        verifyNoInteractions(repo);
    }
}
