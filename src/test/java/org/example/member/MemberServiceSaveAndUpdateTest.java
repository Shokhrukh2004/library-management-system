package org.example.member;

import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.loan.repository.LoanRepository;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberUpdateRequest;
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
public class MemberServiceSaveAndUpdateTest {

    @Mock
    MemberRepository memberRepo;

    @Mock
    LoanRepository loanRepo;

    @InjectMocks
    MemberService memberService;


    //      addMember method test cases
    @Test
    void addMember_validMember_memberIsSave(){
        MemberCreateRequest request = new MemberCreateRequest("John Doe", "john@mail.ru");

        when(memberRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        memberService.addMember(request);

        verify(memberRepo).save(captor.capture());

        Member member = captor.getValue();

        assertAll(
                () -> assertEquals(request.getName(), member.getName()),
                () -> assertEquals(request.getEmail(), member.getEmail())
        );
    }

    @Test
    void addMember_emailAlreadyExists_throwsConflictException(){
        MemberCreateRequest request = new MemberCreateRequest("John Doe", "john@mail.ru");

        when(memberRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(new Member()));

        assertThrows(ConflictException.class, () -> memberService.addMember(request));

        verify(memberRepo).findByEmail(request.getEmail());
        verify(memberRepo, never()).save(any(Member.class));
    }

    @Test
    void addMember_invalidEmail_throwsValidationException(){
        MemberCreateRequest request = new MemberCreateRequest("joe", "abc123");

        assertThrows(ValidationException.class, () -> memberService.addMember(request));

        verifyNoInteractions(memberRepo);
    }


    //      updateMember method test cases
    @Test
    void updateMember_validMember_memberIsUpdated(){
        MemberUpdateRequest request = new MemberUpdateRequest(1, "John Doe", "john@mail.ru");
        Member member = new Member(1, "John", "joe@mail.ru", LocalDate.now(), true);

        when(memberRepo.findById(request.getId())).thenReturn(Optional.of(member));
        when(memberRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        memberService.update(request);

        verify(memberRepo).update(captor.capture());

        Member updatedMember = captor.getValue();

        assertAll(
                () -> assertEquals(request.getId(), updatedMember.getId()),
                () -> assertEquals(request.getName(), updatedMember.getName()),
                () -> assertEquals(request.getEmail(), updatedMember.getEmail()),
                () -> assertEquals(member.getRegisterDate(), updatedMember.getRegisterDate()),
                () -> assertTrue(member.isActive())
        );
    }

    @Test
    void updateMember_memberDoesNotExist_throwsNotFoundException(){
        MemberUpdateRequest request = new MemberUpdateRequest(1, "John Doe", "john@mail.ru");

        when(memberRepo.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberService.update(request));

        verify(memberRepo, never()).update(any(Member.class));
    }

    @Test
    void updateMember_duplicateEmail_throwsConflictException(){
        MemberUpdateRequest request = new MemberUpdateRequest(1, "John Doe", "john@mail.ru");

        when(memberRepo.findById(request.getId())).thenReturn(Optional.of(new Member()));
        when(memberRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(new Member()));

        assertThrows(ConflictException.class, () -> memberService.update(request));

        verify(memberRepo, never()).update(any(Member.class));
    }

    @Test
    void updateMember_invalidEmail_throwsValidationException(){
        MemberUpdateRequest request = new MemberUpdateRequest(1, "joe", "john");

        assertThrows(ValidationException.class, () -> memberService.update(request));

        verifyNoInteractions(memberRepo);
    }
}
