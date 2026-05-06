package org.example.member;

import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberResponse;
import org.example.member.dto.MemberUpdateRequest;
import org.example.member.repository.MemberRepository;
import org.example.validation.MemberValidator;
import org.example.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.member.MemberParser.toMemberFromCreateRequest;
import static org.example.member.MemberParser.toResponseFromMember;

@Service
public class MemberService {
    private final MemberRepository repo;
    private final LoanRepository loanRepo;

    public MemberService(MemberRepository memberRepository, LoanRepository loanRepo) {
        this.repo = memberRepository;
        this.loanRepo = loanRepo;
    }

    public void addMember(MemberCreateRequest member){
        MemberValidator.validateCreateRequest(member);
        emailDuplicateCheck(member.getEmail(), -1);

        repo.save(toMemberFromCreateRequest(member));
    }

    public MemberResponse findById(int id){
        Validator.validatePositiveInt(id, "Id");
        Member member = repo.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Member not found with id: " + id));

        return toResponseFromMember(member);
    }

    public List<MemberResponse> findByName(String name){
        Validator.validateString(name, "Name");

        List<Member> members = repo.findByName(name);
        if(members.isEmpty()){
            throw new NotFoundException("Member not found with name: " + name);
        }

        return members.stream()
                .map(MemberParser::toResponseFromMember)
                .toList();
    }

    public MemberResponse findByEmail(String email){
        MemberValidator.validateEmail(email);
        Member member = repo.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("Member not found with email: " + email));

        return toResponseFromMember(member);
    }

    public List<MemberResponse> findAll(){
        List<Member> members = repo.findAll();
        if(members.isEmpty()){
            throw new NotFoundException("No Members Found");
        }

        return members.stream()
                .map(MemberParser::toResponseFromMember)
                .toList();
    }

    public void update(MemberUpdateRequest member){
        MemberValidator.validateUpdateRequest(member);
        emailDuplicateCheck(member.getEmail(), member.getId());
        Member updateMember = repo.findById(member.getId())
                .orElseThrow(() ->
                        new NotFoundException("Member not found with id: " + member.getId()));

        updateMember.setName(member.getName());
        updateMember.setEmail(member.getEmail());

        repo.update(updateMember);
    }

    public void deactivate(int id){
        Validator.validatePositiveInt(id, "Id");
        Member member = repo.findById(id)
                .orElseThrow(() -> new
                        NotFoundException("Member not found with id: " + id));

        if(!member.isActive()){
            throw new ConflictException("Member is already deactivated");
        }

        isMemberLoanedCheck(id);
        repo.deactivate(member);
    }

    public void activate(int id){
        Validator.validatePositiveInt(id, "Id");
        Member member = repo.findById(id)
                .orElseThrow(() -> new
                        NotFoundException("Member not found with id: " + id));
        if(member.isActive()){
            throw new ConflictException("Member is already activated");
        }

        repo.activate(member);
    }

    public List<MemberResponse> findAllInactive(){
        List<Member> members = repo.findInactiveMembers();
        if(members.isEmpty()){
            throw new NotFoundException("Inactive Members not found");
        }

        return members.stream()
                .map(MemberParser::toResponseFromMember)
                .toList();
    }

    private void emailDuplicateCheck(String email, int excludeId){
        boolean isDuplicate =  repo.findAll()
                .stream()
                .filter(member -> member.getId() != excludeId)
                .anyMatch(member -> member.getEmail().equals(email));

        if(isDuplicate){
            throw new ConflictException("The following email already exists: " + email);
        }
    }

    private void isMemberLoanedCheck(int id){
        boolean isLoaned = loanRepo.findByMemberId(id)
                .stream()
                .anyMatch(loan -> loan.getStatus().equals(Status.ACTIVE) ||
                        loan.getStatus().equals(Status.OVERDUE));
        if(isLoaned){
            throw new ConflictException("The member with id "+ id + " has active loan, thus cannot be deleted.");
        }
    }
}
