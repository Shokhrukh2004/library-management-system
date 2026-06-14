package org.example.member;

import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.exception.ValidationException;
import org.example.loan.enums.Status;
import org.example.loan.repository.LoanRepository;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberResponse;
import org.example.member.dto.MemberUpdateRequest;
import org.example.member.repository.MemberRepository;
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
        validateEmail(member.getEmail());
        emailDuplicateCheck(member.getEmail());

        repo.save(toMemberFromCreateRequest(member));
        log.info("Member successfully added - email: {}", member.getEmail());
    }

    public MemberResponse findById(int id){
        Validator.validateInt(id, "Id");

        return toResponseFromMember(getMemberIfExist(id));
    }

    public List<MemberResponse> findByName(String name){
        Validator.validateStr(name, "Name");
        List<Member> members = repo.findByNameContainingIgnoreCase(name);

        isEmptyCheck(members, "Name " + name);

        return members.stream()
                .map(MemberParser::toResponseFromMember)
                .toList();
    }

    public MemberResponse findByEmail(String email){
        Validator.validateStr(email, "Email");
        validateEmail(email);

        Member member = repo.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("Member not found with email: " + email));

        return toResponseFromMember(member);
    }

    public List<MemberResponse> findAll(){
        List<Member> members = repo.findByIsActive(true);

        isEmptyCheck(members, "");

        return members.stream()
                .map(MemberParser::toResponseFromMember)
                .toList();
    }

    public void update(MemberUpdateRequest member){
        log.info("Updating member - memberId: {}", member.getId());
        validateEmail(member.getEmail());

        Member updateMember = getMemberIfExist(member.getId());
        emailDuplicateCheck(member.getEmail());

        updateMember.setName(member.getName());
        updateMember.setEmail(member.getEmail());

        repo.save(updateMember);
        log.info("Member updated successfully - memberId: {}", member.getId());
    }

    public void deactivate(int id){
        log.info("Deactivating member - memberId: {}", id);
        Validator.validateInt(id, "Id");

        Member member = getMemberIfExist(id);

        isMemberActiveCheck(member);
        isMemberLoanedCheck(id);

        member.setActive(false);
        repo.save(member);
        log.info("Member deactivated successfully - memberId: {}", id);
    }

    public void activate(int id){
        log.info("Activating member - memberId: {}", id);
        Validator.validateInt(id, "Id");

        Member member = getMemberIfExist(id);
        isMemberNotActiveCheck(member);

        member.setActive(true);
        repo.save(member);
        log.info("Member activated successfully - memberId: {}", id);
    }

    public List<MemberResponse> findAllInactive(){
        List<Member> members = repo.findByIsActive(false);

        isEmptyCheck(members, "With Status Inactive");

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
        boolean isLoaned = loanRepo.findByMember_Id(id)
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
                .orElseThrow(() -> {
                    log.warn("Member not found - memberId: {}", id);
                    return new NotFoundException("Member not found");
                });
    }

    private void validateEmail(String value) {
        int atIndex = value.indexOf("@");

        if (atIndex <= 0 || atIndex == value.length() - 1) {
            throw new ValidationException("Email must have content before and after @.");
        }

        String domain = value.substring(atIndex + 1);

        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) {
            throw new ValidationException("Email domain must be valid gmail.com");
        }
    }

    private <T> void isEmptyCheck(List<T> items, String fieldName){
        if(items.isEmpty()){
            throw new NotFoundException("Members not found: " + fieldName);
        }
    }
}
