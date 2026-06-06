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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.member.MemberParser.toMemberFromCreateRequest;
import static org.example.member.MemberParser.toResponseFromMember;

@Service
public class MemberService {
    private final static Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepository repo;
    private final LoanRepository loanRepo;

    public MemberService(MemberRepository memberRepository, LoanRepository loanRepo) {
        this.repo = memberRepository;
        this.loanRepo = loanRepo;
    }

    public void addMember(MemberCreateRequest member){
        log.info("Adding new member - email: {}", member.getEmail());
        MemberValidator.validateCreateRequest(member);
        emailDuplicateCheck(member.getEmail());

        repo.save(toMemberFromCreateRequest(member));
        log.info("Member successfully added - email: {}", member.getEmail());
    }

    public MemberResponse findById(int id){
        Validator.validatePositiveInt(id, "Id");

        return toResponseFromMember(getMemberIfExist(id));
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
        log.info("Updating member - memberId: {}", member.getId());
        MemberValidator.validateUpdateRequest(member);

        Member updateMember = getMemberIfExist(member.getId());
        emailDuplicateCheck(member.getEmail());

        updateMember.setName(member.getName());
        updateMember.setEmail(member.getEmail());

        repo.update(updateMember);
        log.info("Member updated successfully - memberId: {}", member.getId());
    }

    public void deactivate(int id){
        log.info("Deactivating member - memberId: {}", id);
        Validator.validatePositiveInt(id, "Id");
        Member member = getMemberIfExist(id);

        isMemberActiveCheck(member);
        isMemberLoanedCheck(id);

        repo.deactivate(id);
        log.info("Member deactivated successfully - memberId: {}", id);
    }

    public void activate(int id){
        log.info("Activating member - memberId: {}", id);
        Validator.validatePositiveInt(id, "Id");

        Member member = getMemberIfExist(id);
        isMemberNotActiveCheck(member);
        isMemberLoanedCheck(id);

        repo.activate(id);
        log.info("Member activated successfully - memberId: {}", id);
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


    private void emailDuplicateCheck(String email){
        if(repo.findByEmail(email).isPresent()){
            log.warn("Email already exists - email: {}", email);
            throw new ConflictException("Email already exists");
        }
    }

    private void isMemberLoanedCheck(int id){
        boolean isLoaned = loanRepo.findByMemberId(id)
                .stream()
                .anyMatch(loan -> loan.getStatus().equals(Status.ACTIVE) ||
                        loan.getStatus().equals(Status.OVERDUE));
        if(isLoaned){
            log.warn("Member has active loan - memberId: {}", id);
            throw new ConflictException("The member with id "+ id + " has active loan");
        }
    }

    private void isMemberActiveCheck(Member member){
        if(!member.isActive()){
            log.warn("Member is not active already - memberId: {}", member.getId());
            throw new ConflictException("Member is already inactive");
        }
    }

    private void isMemberNotActiveCheck(Member member){
        if(member.isActive()){
            log.warn("Member is active already - memberId: {}", member.getId());
            throw new ConflictException("Member is already activated");
        }
    }

    private Member getMemberIfExist(int id){
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found with id: {}" + id));
    }
}
