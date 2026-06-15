package org.example.member;

import org.example.exception.NotFoundException;
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
    private final MemberConflictLogic logic;

    public MemberService(MemberRepository memberRepository, MemberConflictLogic logic) {
        this.repo = memberRepository;
        this.logic = logic;
    }

    public void addMember(MemberCreateRequest member){
        log.info("Adding new member - email: {}", member.getEmail());
        Validator.validateEmail(member.getEmail());

        logic.emailDuplicateCheck(member.getEmail());

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
        Validator.validateStr(email, "Email " + email);
        Validator.validateEmail(email);

        Member member = repo.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("Member not found with email: " + email));

        return toResponseFromMember(member);
    }

    public List<MemberResponse> findAllInactive(){
        List<Member> members = repo.findByIsActive(false);

        isEmptyCheck(members, "With Status Inactive");

        return members.stream()
                .map(MemberParser::toResponseFromMember)
                .toList();
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
        Validator.validateEmail(member.getEmail());

        Member updateMember = getMemberIfExist(member.getId());
        logic.emailDuplicateCheck(member.getEmail());

        updateMember.setName(member.getName());
        updateMember.setEmail(member.getEmail());

        repo.save(updateMember);
        log.info("Member updated successfully - memberId: {}", member.getId());
    }

    public void deactivate(int id){
        log.info("Deactivating member - memberId: {}", id);
        Validator.validateInt(id, "Id");

        Member member = getMemberIfExist(id);

        logic.isMemberActiveCheck(member);
        logic.isMemberLoanedCheck(id);

        member.setActive(false);
        repo.save(member);
        log.info("Member deactivated successfully - memberId: {}", id);
    }

    public void activate(int id){
        log.info("Activating member - memberId: {}", id);
        Validator.validateInt(id, "Id");

        Member member = getMemberIfExist(id);
        logic.isMemberNotActiveCheck(member);

        member.setActive(true);
        repo.save(member);
        log.info("Member activated successfully - memberId: {}", id);
    }



    private Member getMemberIfExist(int id){
        return repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Member not found - memberId: {}", id);
                    return new NotFoundException("Member not found");
                });
    }

    private <T> void isEmptyCheck(List<T> items, String fieldName){
        if(items.isEmpty()){
            throw new NotFoundException("Members not found: " + fieldName);
        }
    }
}
